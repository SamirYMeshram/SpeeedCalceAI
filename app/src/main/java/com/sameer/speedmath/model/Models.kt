package com.sameer.speedmath.model

import kotlin.math.roundToInt

enum class RootTab(val label: String, val icon: String) {
    PRACTISE("Practise", "⌁"), REVISION("Revision", "▣"), CHALLENGE("Quiz", "◆"), DASHBOARD("Dashboard", "◒"), ACCOUNT("Account", "◎")
}

enum class ModuleCategory(val title: String) { MISC("Miscellaneous"), QUICK("Quick Recall"), BASICS("Basics") }
enum class Difficulty { EASY, MEDIUM, HARD }
enum class InputMode { KEYPAD, OPTIONS }
enum class AnswerType { EXACT_INT, APPROX_INT, DECIMAL, TEXT }
enum class SessionMode { PRACTICE, ERROR_PRACTICE, WORKOUT, DAILY, BATTLE }
enum class SetupType { SIMPLE, RANGE, TRIG, PERCENTAGE, MULTI_SELECT, DI_ADDITION, COMPLEXITY }
enum class QuestionMode { NUMBER_RANGE, RANDOM }


enum class ArithmeticMemoryQuestionType(val label: String, val symbol: String) {
    ADDITION("Addition", "+"),
    SUBTRACTION("Subtraction", "−"),
    ADD_SUBTRACT("Add & Subtract", "↘"),
    MULTIPLICATION("Multiplication", "×"),
    MULTIPLICATION_DIVISION("Multiplication & Division", "⇄"),
    MIXED("Mixed", "✦")
}

enum class ArithmeticMemorySort { NEWEST, OLDEST, ACCURACY_HIGH, TITLE }

data class ArithmeticMemoryConfig(
    val difficulty: Difficulty = Difficulty.HARD,
    val questionType: ArithmeticMemoryQuestionType = ArithmeticMemoryQuestionType.ADDITION
) {
    val stepCount: Int get() = when (difficulty) { Difficulty.EASY -> 10; Difficulty.MEDIUM -> 15; Difficulty.HARD -> 20 }
    val startPaceSeconds: Double get() = 3.0
    val fastestSeconds: Double get() = when (difficulty) { Difficulty.EASY -> 2.0; Difficulty.MEDIUM -> 1.5; Difficulty.HARD -> 1.0 }
}

data class ArithmeticMemoryStep(
    val index: Int,
    val display: String,
    val numericValue: Int,
    val runningTotal: Int
)

data class ArithmeticMemoryRoundResult(
    val id: String,
    val config: ArithmeticMemoryConfig,
    val steps: List<ArithmeticMemoryStep>,
    val correctTotal: Int,
    val userAnswer: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isCorrect: Boolean get() = userAnswer.toIntOrNull() == correctTotal
    val accuracy: Int get() = if (isCorrect) 100 else 0
    val title: String get() = "${config.difficulty.name.lowercase().replaceFirstChar { it.uppercase() }} • ${config.questionType.label}"
}

data class PracticeModule(
    val id: String,
    val title: String,
    val category: ModuleCategory,
    val icon: String,
    val setupType: SetupType,
    val description: String = "",
    val supportsOptions: Boolean = true,
    val answerType: AnswerType = AnswerType.EXACT_INT
)

data class SessionConfig(
    val moduleId: String,
    val moduleTitle: String,
    val mode: SessionMode = SessionMode.PRACTICE,
    val difficulty: Difficulty = Difficulty.EASY,
    val questionCount: Int = 10,
    val questionMode: QuestionMode = QuestionMode.NUMBER_RANGE,
    val from: Int = 5,
    val to: Int = 25,
    val percentageTypes: Set<String> = setOf("A", "B"),
    val selectedModuleIds: Set<String> = emptySet(),
    val angleLevel: String = "Simple angles",
    val numbersToAdd: Int = 2,
    val aMin: Int = 2,
    val aMax: Int = 99,
    val bMin: Int = 101,
    val bMax: Int = 199,
    val operator: String = "×",
    val totalTimeSeconds: Int? = null
)

data class Question(
    val id: String,
    val moduleId: String,
    val moduleTitle: String,
    val text: String,
    val correctAnswer: String,
    val options: List<String> = emptyList(),
    val answerType: AnswerType = AnswerType.EXACT_INT,
    val tolerance: Int = 0,
    val subSkillId: String = "general",
    val explanation: String = ""
)

data class QuestionAttempt(
    val id: String,
    val sessionId: String,
    val moduleId: String,
    val subSkillId: String,
    val questionText: String,
    val correctAnswer: String,
    val userAnswer: String,
    val isCorrect: Boolean,
    val isSkipped: Boolean,
    val timeTakenMs: Long,
    val createdAt: Long = System.currentTimeMillis()
)

data class PracticeSession(
    val id: String,
    val mode: SessionMode,
    val moduleId: String,
    val moduleTitle: String,
    val startedAt: Long,
    val endedAt: Long,
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val skippedCount: Int,
    val score: Double,
    val accuracy: Double,
    val avgTimeMs: Long,
    val totalDurationMs: Long,
    val attempts: List<QuestionAttempt>
)

data class ErrorBankItem(
    val id: String,
    val moduleId: String,
    val subSkillId: String,
    val questionText: String,
    val correctAnswer: String,
    val lastWrongAnswer: String,
    val mistakeCount: Int,
    val solvedAgainCount: Int,
    val lastMistakeAt: Long,
    val resolved: Boolean = false
)

data class UserProfile(
    val name: String = "Sam Meshram",
    val email: String = "sam@example.com",
    val points: Int = 65,
    val level: String = "Beginner",
    val currentStreak: Int = 1,
    val bestStreak: Int = 4,
    val rank30d: String = "--",
    val lifetimeRank: String = "--",
    val adFreeActive: Boolean = true,
    val speedMathId: String = "SM-5253-SAM"
)

data class UserSettings(
    val themeMode: String = "Dark",
    val language: String = "System Default",
    val autoSubmit: Boolean = true,
    val soundEffects: Boolean = true,
    val notifications: Boolean = true
)

data class DashboardSummary(
    val totalQuestions: Int,
    val correct: Int,
    val wrong: Int,
    val skipped: Int,
    val accuracy: Double,
    val avgTimeMs: Long,
    val todayQuestions: Int,
    val todayAccuracy: Double,
    val todayAvgTimeMs: Long,
    val weeklyQuestions: Int,
    val weeklyAccuracy: Double,
    val weeklyAvgTimeMs: Long,
    val focusTopic: String,
    val strongTopic: String
)

data class ChartPoint(val label: String, val value: Float)

data class SessionRuntime(
    val id: String,
    val config: SessionConfig,
    val questions: List<Question>,
    val currentIndex: Int = 0,
    val inputMode: InputMode = InputMode.KEYPAD,
    val typedAnswer: String = "",
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val skippedCount: Int = 0,
    val startedAt: Long = System.currentTimeMillis(),
    val questionStartedAt: Long = System.currentTimeMillis(),
    val elapsedMs: Long = 0L,
    val attempts: List<QuestionAttempt> = emptyList(),
    val completed: Boolean = false
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
}

fun scoreOf(correct: Int, wrong: Int): Double = (correct * 2.0) - (wrong * 0.5)
fun accuracyOf(correct: Int, total: Int): Double = if (total == 0) 0.0 else (correct * 100.0 / total)
fun Double.oneDecimal(): String = "%.1f".format(this)
fun Double.twoDecimal(): String = "%.2f".format(this)
fun Long.secLabel(): String = "%.1fs".format(this / 1000.0)
fun Long.clockLabel(): String {
    val totalSeconds = (this / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val centis = ((this % 1000) / 10).toInt()
    return "%02d:%02d.%02d".format(minutes, seconds, centis)
}
fun Long.shortDurationLabel(): String {
    val seconds = (this / 1000.0).roundToInt()
    val m = seconds / 60
    val s = seconds % 60
    return if (m > 0) "%dm %02ds".format(m, s) else "%02ds".format(s)
}
