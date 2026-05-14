package com.sameer.speedmath.data

import android.content.Context
import com.sameer.speedmath.logic.SearchSortEngine
import com.sameer.speedmath.model.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import kotlin.random.Random

class SpeedMathRepository(context: Context) {
    private val db = NativeSpeedMathDatabase(context.applicationContext).also { it.ensureSeeded() }
    val modules: List<PracticeModule> = listOf(
        PracticeModule("simplification", "Simplification", ModuleCategory.MISC, "∑", SetupType.SIMPLE, "Mixed aptitude simplification"),
        PracticeModule("series", "Series", ModuleCategory.MISC, "⇄", SetupType.SIMPLE, "Number pattern practice"),
        PracticeModule("quadratic", "Quadratic Equation", ModuleCategory.MISC, "x²", SetupType.SIMPLE, "Root and equation drills"),
        PracticeModule("misc_mix", "Miscellaneous Mix", ModuleCategory.MISC, "◇", SetupType.MULTI_SELECT, "Mix simplification, series and quadratic"),
        PracticeModule("square", "Square", ModuleCategory.QUICK, "²", SetupType.RANGE, "Square recall"),
        PracticeModule("cube", "Cube", ModuleCategory.QUICK, "³", SetupType.RANGE, "Cube recall"),
        PracticeModule("square_root", "Square Root", ModuleCategory.QUICK, "√", SetupType.RANGE, "Perfect square root recall"),
        PracticeModule("cube_root", "Cube Root", ModuleCategory.QUICK, "∛", SetupType.RANGE, "Perfect cube root recall"),
        PracticeModule("table", "Table", ModuleCategory.QUICK, "×", SetupType.RANGE, "Multiplication table recall"),
        PracticeModule("trigonometry", "Trigonometry", ModuleCategory.QUICK, "△", SetupType.TRIG, "Standard trigonometric values", answerType = AnswerType.TEXT),
        PracticeModule("percentage", "Percentage", ModuleCategory.QUICK, "%", SetupType.PERCENTAGE, "Percentage and ratio to percent", answerType = AnswerType.APPROX_INT),
        PracticeModule("fraction", "Fraction", ModuleCategory.QUICK, "½", SetupType.SIMPLE, "Common fraction conversions"),
        PracticeModule("di_addition", "DI Addition", ModuleCategory.QUICK, "+", SetupType.DI_ADDITION, "Data interpretation addition"),
        PracticeModule("quick_workout", "Quick Recall Workout", ModuleCategory.QUICK, "⚡", SetupType.MULTI_SELECT, "Mixed memory sprint"),
        PracticeModule("addition", "Addition", ModuleCategory.BASICS, "+", SetupType.SIMPLE, "Addition speed"),
        PracticeModule("subtraction", "Subtraction", ModuleCategory.BASICS, "−", SetupType.SIMPLE, "Subtraction speed"),
        PracticeModule("multiplication", "Multiplication", ModuleCategory.BASICS, "×", SetupType.SIMPLE, "Multiplication speed"),
        PracticeModule("division", "Division", ModuleCategory.BASICS, "÷", SetupType.SIMPLE, "Division speed"),
        PracticeModule("complexity", "Complexity", ModuleCategory.BASICS, "▦", SetupType.COMPLEXITY, "Build your own range drill"),
        PracticeModule("basics_workout", "Basics Workout", ModuleCategory.BASICS, "◎", SetupType.MULTI_SELECT, "Mixed basic operation sprint")
    )

    var profile: UserProfile = UserProfile()
    var settings: UserSettings = UserSettings()
        private set

    private val _sessions = mutableListOf<PracticeSession>()
    private val _errors = mutableListOf<ErrorBankItem>()
    val sessions: List<PracticeSession> get() = _sessions.sortedByDescending { it.startedAt }
    val errorBank: List<ErrorBankItem> get() = _errors.filter { !it.resolved }.sortedWith(compareByDescending<ErrorBankItem> { it.mistakeCount }.thenByDescending { it.lastMistakeAt })
    val arithmeticMemoryRounds: List<ArithmeticMemoryRoundResult> get() = db.arithmeticRounds()

    init {
        seedHistory()
        _sessions.flatMap { it.attempts }.filter { !it.isCorrect && !it.isSkipped }.take(20).forEach { updateErrorBank(it) }
    }

    fun module(id: String): PracticeModule = modules.first { it.id == id }
    fun toggleAutoSubmit() { settings = settings.copy(autoSubmit = !settings.autoSubmit) }
    fun toggleSounds() { settings = settings.copy(soundEffects = !settings.soundEffects) }

    fun saveArithmeticMemoryRound(result: ArithmeticMemoryRoundResult) {
        db.saveArithmeticRound(result)
    }

    fun revisionRecords(topicSlug: String, query: String = "", sort: RevisionSort = RevisionSort.DEFAULT_ORDER): List<RevisionRecord> {
        val records = db.revisionByTopic(topicSlug)
        return SearchSortEngine.revision(records, query, sort)
    }

    fun markRevisionViewed(recordId: Long) {
        db.updateRevisionFrequency(recordId)
    }

    fun saveSession(session: PracticeSession) {
        _sessions.add(session)
        session.attempts.filter { !it.isCorrect && !it.isSkipped }.forEach { updateErrorBank(it) }
        session.attempts.filter { it.isCorrect }.forEach { correctErrorIfPresent(it) }
    }

    private fun updateErrorBank(attempt: QuestionAttempt) {
        val existingIndex = _errors.indexOfFirst { it.questionText == attempt.questionText && it.correctAnswer == attempt.correctAnswer && !it.resolved }
        if (existingIndex >= 0) {
            val e = _errors[existingIndex]
            _errors[existingIndex] = e.copy(
                lastWrongAnswer = attempt.userAnswer,
                mistakeCount = e.mistakeCount + 1,
                lastMistakeAt = System.currentTimeMillis(),
                solvedAgainCount = 0
            )
        } else {
            _errors.add(
                ErrorBankItem(
                    id = UUID.randomUUID().toString(), moduleId = attempt.moduleId,
                    subSkillId = attempt.subSkillId, questionText = attempt.questionText,
                    correctAnswer = attempt.correctAnswer, lastWrongAnswer = attempt.userAnswer,
                    mistakeCount = 1, solvedAgainCount = 0, lastMistakeAt = System.currentTimeMillis()
                )
            )
        }
    }

    private fun correctErrorIfPresent(attempt: QuestionAttempt) {
        val i = _errors.indexOfFirst { it.questionText == attempt.questionText && it.correctAnswer == attempt.correctAnswer && !it.resolved }
        if (i >= 0) {
            val e = _errors[i]
            val solved = e.solvedAgainCount + 1
            _errors[i] = e.copy(solvedAgainCount = solved, resolved = solved >= 3)
        }
    }

    private fun seedHistory() {
        val zone = ZoneId.systemDefault()
        fun time(daysAgo: Long, hour: Int, minute: Int): Long = LocalDateTime.now().minusDays(daysAgo).withHour(hour).withMinute(minute).withSecond(0).atZone(zone).toInstant().toEpochMilli()
        fun fakeAttempt(sessionId: String, module: String, title: String, q: String, c: String, u: String, correct: Boolean, ms: Long): QuestionAttempt = QuestionAttempt(
            id = UUID.randomUUID().toString(), sessionId = sessionId, moduleId = module, subSkillId = if (module == "multiplication") "2_digit_by_2_digit" else "general",
            questionText = q, correctAnswer = c, userAnswer = u, isCorrect = correct, isSkipped = false, timeTakenMs = ms, createdAt = System.currentTimeMillis()
        )
        val multiplicationId = UUID.randomUUID().toString()
        val multAttempts = listOf(
            fakeAttempt(multiplicationId, "multiplication", "Multiplication", "95 × 85 = ?", "8075", "7175", false, 17800),
            fakeAttempt(multiplicationId, "multiplication", "Multiplication", "72 × 63 = ?", "4536", "4536", true, 12600),
            fakeAttempt(multiplicationId, "multiplication", "Multiplication", "93 × 91 = ?", "8463", "8346", false, 10300),
            fakeAttempt(multiplicationId, "multiplication", "Multiplication", "62 × 89 = ?", "5518", "5518", true, 21000),
            fakeAttempt(multiplicationId, "multiplication", "Multiplication", "56 × 92 = ?", "5152", "5132", false, 16900),
            fakeAttempt(multiplicationId, "multiplication", "Multiplication", "63 × 67 = ?", "4221", "3861", false, 15700),
            fakeAttempt(multiplicationId, "multiplication", "Multiplication", "87 × 46 = ?", "4002", "4002", true, 9200),
            fakeAttempt(multiplicationId, "multiplication", "Multiplication", "44 × 74 = ?", "3256", "3256", true, 8800),
            fakeAttempt(multiplicationId, "multiplication", "Multiplication", "81 × 34 = ?", "2754", "2754", true, 11300),
            fakeAttempt(multiplicationId, "multiplication", "Multiplication", "39 × 28 = ?", "1092", "1092", true, 12100),
            fakeAttempt(multiplicationId, "multiplication", "Multiplication", "98 × 37 = ?", "3626", "3626", true, 18900),
            fakeAttempt(multiplicationId, "multiplication", "Multiplication", "77 × 88 = ?", "6776", "6766", false, 14300),
            fakeAttempt(multiplicationId, "multiplication", "Multiplication", "57 × 64 = ?", "3648", "3648", true, 14600),
            fakeAttempt(multiplicationId, "multiplication", "Multiplication", "69 × 59 = ?", "4071", "4071", true, 15600),
            fakeAttempt(multiplicationId, "multiplication", "Multiplication", "73 × 36 = ?", "2628", "2628", true, 13200),
            fakeAttempt(multiplicationId, "multiplication", "Multiplication", "92 × 54 = ?", "4968", "4868", false, 22700)
        )
        _sessions.add(sessionFromAttempts(multiplicationId, SessionMode.PRACTICE, "multiplication", "Multiplication", time(0, 15, 52), multAttempts))

        val addId = UUID.randomUUID().toString()
        val addAttempts = (1..10).map { i ->
            val a = 80 + i * 7; val b = 15 + i * 3; val correct = i != 4
            fakeAttempt(addId, "addition", "Addition", "$a + $b = ?", "${a + b}", if (correct) "${a + b}" else "${a + b + 4}", correct, 2200 + i * 180L)
        }
        _sessions.add(sessionFromAttempts(addId, SessionMode.PRACTICE, "addition", "Addition", time(0, 14, 37), addAttempts))

        val oldId = UUID.randomUUID().toString()
        val oldAttempts = (1..234).map { i ->
            val correct = i % 11 != 0
            fakeAttempt(oldId, "addition", "Addition", "${Random.nextInt(10, 500)} + ${Random.nextInt(10, 500)} = ?", "100", if (correct) "100" else "101", correct, Random.nextLong(1800, 6000))
        }
        _sessions.add(sessionFromAttempts(oldId, SessionMode.PRACTICE, "addition", "Addition", time(4, 13, 45), oldAttempts))

        val modulesForSeed = listOf("simplification", "subtraction", "di_addition", "series", "cube", "square", "quadratic", "trigonometry", "mixed_question")
        repeat(14) { index ->
            val mod = modulesForSeed[index % modulesForSeed.size]
            val title = modules.find { it.id == mod }?.title ?: "Mixed Question"
            val id = UUID.randomUUID().toString()
            val attempts = (1..Random.nextInt(8, 36)).map { q ->
                val correct = Random.nextInt(100) > 13
                fakeAttempt(id, mod, title, "${Random.nextInt(20, 190)} - ${Random.nextInt(1, 19)} = ?", "80", if (correct) "80" else "82", correct, Random.nextLong(1800, 9000))
            }
            _sessions.add(sessionFromAttempts(id, if (index % 5 == 0) SessionMode.ERROR_PRACTICE else SessionMode.PRACTICE, mod, title, time(Random.nextLong(1, 29), Random.nextInt(9, 22), Random.nextInt(0, 58)), attempts))
        }
    }

    private fun sessionFromAttempts(id: String, mode: SessionMode, moduleId: String, title: String, start: Long, attempts: List<QuestionAttempt>): PracticeSession {
        val correct = attempts.count { it.isCorrect }
        val wrong = attempts.count { !it.isCorrect && !it.isSkipped }
        val skipped = attempts.count { it.isSkipped }
        val duration = attempts.sumOf { it.timeTakenMs }.coerceAtLeast(1000)
        return PracticeSession(
            id = id, mode = mode, moduleId = moduleId, moduleTitle = title, startedAt = start, endedAt = start + duration,
            totalQuestions = attempts.size, correctCount = correct, wrongCount = wrong, skippedCount = skipped,
            score = scoreOf(correct, wrong), accuracy = accuracyOf(correct, attempts.size),
            avgTimeMs = if (attempts.isEmpty()) 0 else attempts.sumOf { it.timeTakenMs } / attempts.size,
            totalDurationMs = duration, attempts = attempts.map { it.copy(createdAt = start) }
        )
    }
}
