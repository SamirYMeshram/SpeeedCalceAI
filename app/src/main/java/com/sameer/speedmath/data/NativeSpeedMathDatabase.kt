package com.sameer.speedmath.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.sameer.speedmath.model.ArithmeticMemoryConfig
import com.sameer.speedmath.model.ArithmeticMemoryQuestionType
import com.sameer.speedmath.model.ArithmeticMemoryRoundResult
import com.sameer.speedmath.model.ArithmeticMemoryStep
import com.sameer.speedmath.model.Difficulty
import com.sameer.speedmath.model.RevisionContentType
import com.sameer.speedmath.model.RevisionRecord
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Native Android SQLite storage. No Room, no ORM, no external database wrapper.
 * This is the persistent source for revision content and arithmetic-memory history.
 */
class NativeSpeedMathDatabase(private val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE revision_items(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                topic_slug TEXT NOT NULL,
                topic_title TEXT NOT NULL,
                category TEXT NOT NULL,
                content_type TEXT NOT NULL,
                left_text TEXT NOT NULL,
                right_text TEXT NOT NULL,
                note TEXT NOT NULL DEFAULT '',
                order_index INTEGER NOT NULL,
                mastery INTEGER NOT NULL DEFAULT 0,
                frequency INTEGER NOT NULL DEFAULT 0,
                updated_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_revision_topic ON revision_items(topic_slug, order_index)")
        db.execSQL("CREATE INDEX idx_revision_search ON revision_items(left_text, right_text, topic_title)")

        db.execSQL(
            """
            CREATE TABLE arithmetic_rounds(
                id TEXT PRIMARY KEY,
                difficulty TEXT NOT NULL,
                question_type TEXT NOT NULL,
                correct_total INTEGER NOT NULL,
                user_answer TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE arithmetic_steps(
                round_id TEXT NOT NULL,
                step_index INTEGER NOT NULL,
                display TEXT NOT NULL,
                numeric_value INTEGER NOT NULL,
                running_total INTEGER NOT NULL,
                PRIMARY KEY(round_id, step_index)
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS arithmetic_steps")
        db.execSQL("DROP TABLE IF EXISTS arithmetic_rounds")
        db.execSQL("DROP TABLE IF EXISTS revision_items")
        onCreate(db)
    }

    fun ensureSeeded() {
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM revision_items", null)
        val count = cursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
        if (count > 0) return
        db.beginTransaction()
        try {
            seedRevision(db)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun revisionByTopic(topicSlug: String): List<RevisionRecord> {
        ensureSeeded()
        val db = readableDatabase
        val cursor = db.query(
            "revision_items",
            null,
            "topic_slug=?",
            arrayOf(topicSlug),
            null,
            null,
            "order_index ASC, id ASC"
        )
        return cursor.use { c -> buildList { while (c.moveToNext()) add(c.toRevisionRecord()) } }
    }

    fun updateRevisionFrequency(id: Long) {
        val db = writableDatabase
        db.execSQL("UPDATE revision_items SET frequency = frequency + 1, updated_at = ? WHERE id = ?", arrayOf(System.currentTimeMillis(), id))
    }

    fun saveArithmeticRound(result: ArithmeticMemoryRoundResult) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val round = ContentValues().apply {
                put("id", result.id)
                put("difficulty", result.config.difficulty.name)
                put("question_type", result.config.questionType.name)
                put("correct_total", result.correctTotal)
                put("user_answer", result.userAnswer)
                put("created_at", result.createdAt)
            }
            db.insertWithOnConflict("arithmetic_rounds", null, round, SQLiteDatabase.CONFLICT_REPLACE)
            result.steps.forEach { step ->
                val values = ContentValues().apply {
                    put("round_id", result.id)
                    put("step_index", step.index)
                    put("display", step.display)
                    put("numeric_value", step.numericValue)
                    put("running_total", step.runningTotal)
                }
                db.insertWithOnConflict("arithmetic_steps", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun arithmeticRounds(): List<ArithmeticMemoryRoundResult> {
        val db = readableDatabase
        val rounds = db.query("arithmetic_rounds", null, null, null, null, null, "created_at DESC").use { c ->
            buildList {
                while (c.moveToNext()) {
                    val id = c.getString(c.getColumnIndexOrThrow("id"))
                    val difficulty = Difficulty.valueOf(c.getString(c.getColumnIndexOrThrow("difficulty")))
                    val type = ArithmeticMemoryQuestionType.valueOf(c.getString(c.getColumnIndexOrThrow("question_type")))
                    val correct = c.getInt(c.getColumnIndexOrThrow("correct_total"))
                    val answer = c.getString(c.getColumnIndexOrThrow("user_answer"))
                    val createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"))
                    add(PartialRound(id, difficulty, type, correct, answer, createdAt))
                }
            }
        }
        return rounds.map { r ->
            ArithmeticMemoryRoundResult(
                id = r.id,
                config = ArithmeticMemoryConfig(r.difficulty, r.type),
                steps = stepsForRound(db, r.id),
                correctTotal = r.correctTotal,
                userAnswer = r.userAnswer,
                createdAt = r.createdAt
            )
        }
    }

    private fun stepsForRound(db: SQLiteDatabase, roundId: String): List<ArithmeticMemoryStep> {
        val c = db.query("arithmetic_steps", null, "round_id=?", arrayOf(roundId), null, null, "step_index ASC")
        return c.use {
            buildList {
                while (it.moveToNext()) {
                    add(
                        ArithmeticMemoryStep(
                            index = it.getInt(it.getColumnIndexOrThrow("step_index")),
                            display = it.getString(it.getColumnIndexOrThrow("display")),
                            numericValue = it.getInt(it.getColumnIndexOrThrow("numeric_value")),
                            runningTotal = it.getInt(it.getColumnIndexOrThrow("running_total"))
                        )
                    )
                }
            }
        }
    }

    private fun seedRevision(db: SQLiteDatabase) {
        context.assets.open("revision_seed.tsv").use { input ->
            BufferedReader(InputStreamReader(input)).useLines { lines ->
                lines.drop(1).filter { it.isNotBlank() && !it.startsWith("#") }.forEach { raw ->
                    val parts = raw.split('\t')
                    if (parts.size < 9) return@forEach
                    val values = ContentValues().apply {
                        put("topic_slug", parts[0])
                        put("topic_title", parts[1])
                        put("category", parts[2])
                        put("content_type", parts[3])
                        put("left_text", parts[4])
                        put("right_text", parts[5])
                        put("note", parts[6])
                        put("order_index", parts[7].toIntOrNull() ?: 0)
                        put("mastery", parts[8].toIntOrNull() ?: 0)
                        put("frequency", 0)
                        put("updated_at", System.currentTimeMillis())
                    }
                    db.insert("revision_items", null, values)
                }
            }
        }
    }

    private fun Cursor.toRevisionRecord(): RevisionRecord = RevisionRecord(
        id = getLong(getColumnIndexOrThrow("id")),
        topicSlug = getString(getColumnIndexOrThrow("topic_slug")),
        topicTitle = getString(getColumnIndexOrThrow("topic_title")),
        category = getString(getColumnIndexOrThrow("category")),
        contentType = RevisionContentType.valueOf(getString(getColumnIndexOrThrow("content_type"))),
        leftText = getString(getColumnIndexOrThrow("left_text")),
        rightText = getString(getColumnIndexOrThrow("right_text")),
        note = getString(getColumnIndexOrThrow("note")),
        orderIndex = getInt(getColumnIndexOrThrow("order_index")),
        mastery = getInt(getColumnIndexOrThrow("mastery")),
        frequency = getInt(getColumnIndexOrThrow("frequency")),
        updatedAt = getLong(getColumnIndexOrThrow("updated_at"))
    )

    private data class PartialRound(
        val id: String,
        val difficulty: Difficulty,
        val type: ArithmeticMemoryQuestionType,
        val correctTotal: Int,
        val userAnswer: String,
        val createdAt: Long
    )

    companion object {
        private const val DB_NAME = "speed_math_native.db"
        private const val DB_VERSION = 1
    }
}
