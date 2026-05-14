package com.sameer.speedmath.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sameer.speedmath.data.SpeedMathRepository
import com.sameer.speedmath.logic.AnalyticsEngine
import com.sameer.speedmath.logic.SearchSortEngine
import com.sameer.speedmath.logic.QuestionEngine
import com.sameer.speedmath.model.*
import java.util.UUID

class SpeedMathViewModel(private val repository: SpeedMathRepository) {
    var sessions by mutableStateOf(repository.sessions)
        private set
    var settings by mutableStateOf(repository.settings)
        private set
    var arithmeticMemoryRounds by mutableStateOf(repository.arithmeticMemoryRounds)
        private set
    var runtime by mutableStateOf<SessionRuntime?>(null)
        private set
    var completionDialog by mutableStateOf<PracticeSession?>(null)
        private set

    val modules get() = repository.modules
    val profile get() = repository.profile
    val summary get() = AnalyticsEngine.summary(sessions)

    fun module(id: String): PracticeModule = repository.module(id)
    fun dashboardModuleDistribution() = AnalyticsEngine.moduleDistribution(sessions)
    fun accuracyOverTime() = AnalyticsEngine.accuracyOverTime(sessions)
    fun averageTimeOverTime() = AnalyticsEngine.averageTimeOverTime(sessions)
    fun questionsPerDay() = AnalyticsEngine.questionsPerDay(sessions)
    fun durationPerDay() = AnalyticsEngine.durationPerDay(sessions)

    fun startSession(config: SessionConfig) {
        val questions = QuestionEngine.generate(config, repository)
        runtime = SessionRuntime(UUID.randomUUID().toString(), config, questions, inputMode = if (config.mode == SessionMode.WORKOUT) InputMode.KEYPAD else InputMode.KEYPAD)
        completionDialog = null
    }

    fun startErrorPractice() {
        startSession(SessionConfig(moduleId = "error_practice", moduleTitle = "Error Practice", mode = SessionMode.ERROR_PRACTICE, questionCount = 10))
    }

    fun startWorkout() {
        startSession(SessionConfig(moduleId = "workout", moduleTitle = "Workout", mode = SessionMode.WORKOUT, questionCount = 20, totalTimeSeconds = 150, difficulty = Difficulty.MEDIUM))
    }

    fun tick() {
        val r = runtime ?: return
        if (r.completed || completionDialog != null) return
        val now = System.currentTimeMillis()
        val elapsed = now - r.startedAt
        val limit = r.config.totalTimeSeconds?.times(1000L)
        if (limit != null && elapsed >= limit) {
            finishSession()
        } else {
            runtime = r.copy(elapsedMs = elapsed)
        }
    }

    fun appendInput(value: String) {
        val r = runtime ?: return
        if (r.completed || completionDialog != null) return
        val q = r.currentQuestion ?: return
        if (value == "-" && r.typedAnswer.contains("-")) return
        val next = if (value == "-" && r.typedAnswer.isEmpty()) "-" else r.typedAnswer + value
        runtime = r.copy(typedAnswer = next)
        if (settings.autoSubmit && q.answerType != AnswerType.TEXT && q.correctAnswer.length == next.length && next != "-") {
            submitAnswer(next)
        }
    }

    fun backspace() {
        val r = runtime ?: return
        if (r.completed || completionDialog != null) return
        runtime = r.copy(typedAnswer = r.typedAnswer.dropLast(1))
    }

    fun toggleInputMode() {
        val r = runtime ?: return
        if (r.completed || completionDialog != null) return
        runtime = r.copy(inputMode = if (r.inputMode == InputMode.KEYPAD) InputMode.OPTIONS else InputMode.KEYPAD, typedAnswer = "")
    }

    fun submitCurrent() { runtime?.typedAnswer?.takeIf { it.isNotBlank() }?.let { submitAnswer(it) } }
    fun selectOption(option: String) = submitAnswer(option)

    fun skipQuestion() {
        val r = runtime ?: return
        if (r.completed || completionDialog != null) return
        val q = r.currentQuestion ?: return
        val now = System.currentTimeMillis()
        val attempt = QuestionAttempt(
            id = UUID.randomUUID().toString(), sessionId = r.id, moduleId = q.moduleId, subSkillId = q.subSkillId,
            questionText = q.text, correctAnswer = q.correctAnswer, userAnswer = "Skipped", isCorrect = false, isSkipped = true, timeTakenMs = now - r.questionStartedAt
        )
        nextQuestion(r.copy(skippedCount = r.skippedCount + 1, attempts = r.attempts + attempt))
    }

    fun submitAnswer(answer: String) {
        val r = runtime ?: return
        if (r.completed || completionDialog != null) return
        val q = r.currentQuestion ?: return
        val now = System.currentTimeMillis()
        val correct = QuestionEngine.isCorrect(q, answer)
        val attempt = QuestionAttempt(
            id = UUID.randomUUID().toString(), sessionId = r.id, moduleId = q.moduleId, subSkillId = q.subSkillId,
            questionText = q.text, correctAnswer = q.correctAnswer, userAnswer = answer, isCorrect = correct, isSkipped = false, timeTakenMs = now - r.questionStartedAt
        )
        nextQuestion(r.copy(
            correctCount = r.correctCount + if (correct) 1 else 0,
            wrongCount = r.wrongCount + if (!correct) 1 else 0,
            attempts = r.attempts + attempt,
            typedAnswer = ""
        ))
    }

    private fun nextQuestion(r: SessionRuntime) {
        if (r.currentIndex >= r.questions.lastIndex) {
            runtime = r.copy(completed = true, elapsedMs = System.currentTimeMillis() - r.startedAt)
            finishSession()
        } else {
            runtime = r.copy(currentIndex = r.currentIndex + 1, typedAnswer = "", questionStartedAt = System.currentTimeMillis())
        }
    }

    fun finishSession() {
        val r = runtime ?: return
        if (completionDialog != null) return
        val now = System.currentTimeMillis()
        val total = r.questions.size
        val attempts = r.attempts.distinctBy { it.id }.take(total)
        val finalAttempts = if (attempts.size < total) {
            attempts + r.questions.drop(attempts.size).map { q ->
                QuestionAttempt(UUID.randomUUID().toString(), r.id, q.moduleId, q.subSkillId, q.text, q.correctAnswer, "Skipped", false, true, 0)
            }
        } else attempts
        val correct = finalAttempts.count { it.isCorrect }
        val skipped = finalAttempts.count { it.isSkipped }
        val wrong = finalAttempts.count { !it.isCorrect && !it.isSkipped }
        val session = PracticeSession(
            id = r.id, mode = r.config.mode, moduleId = r.config.moduleId, moduleTitle = r.config.moduleTitle,
            startedAt = r.startedAt, endedAt = now, totalQuestions = total, correctCount = correct, wrongCount = wrong,
            skippedCount = skipped, score = scoreOf(correct, wrong), accuracy = accuracyOf(correct, total),
            avgTimeMs = if (finalAttempts.isEmpty()) 0 else finalAttempts.sumOf { it.timeTakenMs } / finalAttempts.size,
            totalDurationMs = now - r.startedAt, attempts = finalAttempts
        )
        repository.saveSession(session)
        sessions = repository.sessions
        completionDialog = session
        runtime = r.copy(
            completed = true,
            currentIndex = (total - 1).coerceAtLeast(0),
            attempts = finalAttempts,
            correctCount = correct,
            wrongCount = wrong,
            skippedCount = skipped,
            elapsedMs = now - r.startedAt
        )
    }

    fun saveArithmeticMemoryRound(result: ArithmeticMemoryRoundResult) {
        repository.saveArithmeticMemoryRound(result)
        arithmeticMemoryRounds = repository.arithmeticMemoryRounds
    }

    fun arithmeticMemoryHistory(query: String = "", sort: ArithmeticMemorySort = ArithmeticMemorySort.NEWEST): List<ArithmeticMemoryRoundResult> =
        SearchSortEngine.arithmetic(arithmeticMemoryRounds, query, sort)

    fun revisionRecords(topicSlug: String, query: String = "", sort: RevisionSort = RevisionSort.DEFAULT_ORDER): List<RevisionRecord> =
        repository.revisionRecords(topicSlug, query, sort)

    fun markRevisionViewed(id: Long) { repository.markRevisionViewed(id) }

    fun clearCompletion() { completionDialog = null; runtime = null }

    fun toggleAutoSubmit() { repository.toggleAutoSubmit(); settings = repository.settings }
    fun toggleSounds() { repository.toggleSounds(); settings = repository.settings }

    fun session(id: String?): PracticeSession? = sessions.firstOrNull { it.id == id }
}
