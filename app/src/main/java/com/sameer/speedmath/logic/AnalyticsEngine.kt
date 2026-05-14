package com.sameer.speedmath.logic

import com.sameer.speedmath.model.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object AnalyticsEngine {
    fun summary(sessions: List<PracticeSession>): DashboardSummary {
        val attempts = sessions.flatMap { it.attempts }
        val total = attempts.size
        val correct = attempts.count { it.isCorrect }
        val skipped = attempts.count { it.isSkipped }
        val wrong = attempts.count { !it.isCorrect && !it.isSkipped }
        val avg = if (attempts.isEmpty()) 0 else attempts.sumOf { it.timeTakenMs } / attempts.size
        val today = LocalDate.now()
        val zone = ZoneId.systemDefault()
        val todayAttempts = attempts.filter { Instant.ofEpochMilli(it.createdAt).atZone(zone).toLocalDate() == today }
        val weeklyAttempts = attempts.filter { !Instant.ofEpochMilli(it.createdAt).atZone(zone).toLocalDate().isBefore(today.minusDays(6)) }
        return DashboardSummary(
            totalQuestions = total,
            correct = correct,
            wrong = wrong,
            skipped = skipped,
            accuracy = accuracyOf(correct, total),
            avgTimeMs = avg,
            todayQuestions = todayAttempts.size,
            todayAccuracy = accuracyOf(todayAttempts.count { it.isCorrect }, todayAttempts.size),
            todayAvgTimeMs = avgMs(todayAttempts),
            weeklyQuestions = weeklyAttempts.size,
            weeklyAccuracy = accuracyOf(weeklyAttempts.count { it.isCorrect }, weeklyAttempts.size),
            weeklyAvgTimeMs = avgMs(weeklyAttempts),
            focusTopic = focusTopic(attempts),
            strongTopic = strongTopic(attempts)
        )
    }

    private fun avgMs(attempts: List<QuestionAttempt>) = if (attempts.isEmpty()) 0 else attempts.sumOf { it.timeTakenMs } / attempts.size

    fun moduleDistribution(sessions: List<PracticeSession>): List<ChartPoint> = sessions
        .groupBy { if (it.mode == SessionMode.BATTLE) "Battle (1v1)" else if (it.mode == SessionMode.ERROR_PRACTICE) "Error Practice" else it.moduleTitle }
        .map { ChartPoint(it.key, it.value.sumOf { s -> s.totalQuestions }.toFloat()) }
        .sortedByDescending { it.value }
        .take(10)

    fun accuracyOverTime(sessions: List<PracticeSession>): List<ChartPoint> = sessions
        .sortedBy { it.startedAt }
        .takeLast(12)
        .mapIndexed { i, s -> ChartPoint("${i + 1}", s.accuracy.toFloat()) }

    fun averageTimeOverTime(sessions: List<PracticeSession>): List<ChartPoint> = sessions
        .sortedBy { it.startedAt }
        .takeLast(12)
        .mapIndexed { i, s -> ChartPoint("${i + 1}", (s.avgTimeMs / 1000f)) }

    fun questionsPerDay(sessions: List<PracticeSession>): List<ChartPoint> {
        val zone = ZoneId.systemDefault()
        return sessions.groupBy { Instant.ofEpochMilli(it.startedAt).atZone(zone).toLocalDate().toString().substring(5) }
            .map { ChartPoint(it.key, it.value.sumOf { s -> s.totalQuestions }.toFloat()) }
            .takeLast(12)
    }

    fun durationPerDay(sessions: List<PracticeSession>): List<ChartPoint> {
        val zone = ZoneId.systemDefault()
        return sessions.groupBy { Instant.ofEpochMilli(it.startedAt).atZone(zone).toLocalDate().toString().substring(5) }
            .map { ChartPoint(it.key, it.value.sumOf { s -> s.totalDurationMs }.toFloat() / 60000f) }
            .takeLast(12)
    }

    private fun focusTopic(attempts: List<QuestionAttempt>): String {
        val sub = attempts.groupBy { it.subSkillId }
            .filter { it.value.size >= 4 }
            .maxByOrNull { (_, items) ->
                val accPenalty = 100.0 - accuracyOf(items.count { it.isCorrect }, items.size)
                val slowPenalty = items.map { it.timeTakenMs }.average() / 1000.0
                accPenalty + slowPenalty
            }?.key ?: "2_digit_by_2_digit"
        return when (sub) {
            "2_digit_by_2_digit" -> "2 Digit By 2 Digit Multiplication"
            "borrow_subtraction" -> "Borrow Subtraction"
            "ratio_to_percentage" -> "Ratio To Percentage"
            else -> sub.replace('_', ' ').replaceFirstChar { it.uppercase() }
        }
    }

    private fun strongTopic(attempts: List<QuestionAttempt>): String = attempts.groupBy { it.moduleId }
        .filter { it.value.size >= 5 }
        .maxByOrNull { (_, items) -> accuracyOf(items.count { it.isCorrect }, items.size) - (items.map { it.timeTakenMs }.average() / 10000.0) }
        ?.key?.replace('_', ' ')?.replaceFirstChar { it.uppercase() } ?: "Addition"
}
