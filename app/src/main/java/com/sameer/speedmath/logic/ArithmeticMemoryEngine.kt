package com.sameer.speedmath.logic

import com.sameer.speedmath.model.ArithmeticMemoryConfig
import com.sameer.speedmath.model.ArithmeticMemoryQuestionType
import com.sameer.speedmath.model.ArithmeticMemoryRoundResult
import com.sameer.speedmath.model.ArithmeticMemorySort
import com.sameer.speedmath.model.ArithmeticMemoryStep
import java.util.UUID
import kotlin.math.max
import kotlin.random.Random

/**
 * Arithmetic Memory is a timed running-total drill: the app flashes a sequence
 * of signed operations, then asks the user to recall the final total.
 */
object ArithmeticMemoryEngine {
    private val referenceHardAddition = listOf(8, 9, 6, 6, 8, 6, 6, 9, 7, 7, 7, 8, 6, 7, 9, 6, 6, 6, 8, 9)

    fun generateSteps(config: ArithmeticMemoryConfig, seed: Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()): List<ArithmeticMemoryStep> {
        val random = Random(seed)
        val operations = when (config.questionType) {
            ArithmeticMemoryQuestionType.ADDITION -> additionSequence(config, random)
            ArithmeticMemoryQuestionType.SUBTRACTION -> List(config.stepCount) { -random.nextInt(3, 10) }
            ArithmeticMemoryQuestionType.ADD_SUBTRACT -> List(config.stepCount) { index ->
                val value = random.nextInt(4, 12)
                if (index % 3 == 1) -value else value
            }
            ArithmeticMemoryQuestionType.MULTIPLICATION -> multiplicationLike(config, random, allowDivide = false)
            ArithmeticMemoryQuestionType.MULTIPLICATION_DIVISION -> multiplicationLike(config, random, allowDivide = true)
            ArithmeticMemoryQuestionType.MIXED -> mixedSequence(config, random)
        }
        var total = if (config.questionType == ArithmeticMemoryQuestionType.MULTIPLICATION) 1 else 0
        return operations.mapIndexed { index, op ->
            val display = displayFor(config.questionType, op)
            total = apply(config.questionType, total, op)
            ArithmeticMemoryStep(index + 1, display, op, total)
        }
    }

    fun buildResult(config: ArithmeticMemoryConfig, steps: List<ArithmeticMemoryStep>, answer: String): ArithmeticMemoryRoundResult {
        val total = steps.lastOrNull()?.runningTotal ?: 0
        return ArithmeticMemoryRoundResult(
            id = UUID.randomUUID().toString(),
            config = config,
            steps = steps,
            correctTotal = total,
            userAnswer = answer.trim()
        )
    }

    fun searchAndSort(
        rounds: List<ArithmeticMemoryRoundResult>,
        query: String,
        sort: ArithmeticMemorySort
    ): List<ArithmeticMemoryRoundResult> {
        val normalized = query.trim().lowercase()
        val filtered = if (normalized.isBlank()) rounds else rounds.filter { round ->
            round.title.lowercase().contains(normalized) ||
                round.correctTotal.toString().contains(normalized) ||
                round.userAnswer.contains(normalized)
        }
        return when (sort) {
            ArithmeticMemorySort.NEWEST -> filtered.sortedByDescending { it.createdAt }
            ArithmeticMemorySort.OLDEST -> filtered.sortedBy { it.createdAt }
            ArithmeticMemorySort.ACCURACY_HIGH -> filtered.sortedWith(compareByDescending<ArithmeticMemoryRoundResult> { it.accuracy }.thenByDescending { it.createdAt })
            ArithmeticMemorySort.TITLE -> filtered.sortedBy { it.title }
        }
    }

    private fun additionSequence(config: ArithmeticMemoryConfig, random: Random): List<Int> {
        if (config.difficulty.name == "HARD") return referenceHardAddition.take(config.stepCount)
        return List(config.stepCount) { random.nextInt(5, 10) }
    }

    private fun multiplicationLike(config: ArithmeticMemoryConfig, random: Random, allowDivide: Boolean): List<Int> =
        List(config.stepCount) { index ->
            if (allowDivide && index > 1 && index % 4 == 0) -random.nextInt(2, 5) else random.nextInt(2, 5)
        }

    private fun mixedSequence(config: ArithmeticMemoryConfig, random: Random): List<Int> =
        List(config.stepCount) { index ->
            when (index % 5) {
                0, 1 -> random.nextInt(5, 12)
                2 -> -random.nextInt(3, 9)
                3 -> random.nextInt(2, 5) * 10
                else -> random.nextInt(4, 10)
            }
        }

    private fun displayFor(type: ArithmeticMemoryQuestionType, value: Int): String = when (type) {
        ArithmeticMemoryQuestionType.MULTIPLICATION -> "×${max(2, value)}"
        ArithmeticMemoryQuestionType.MULTIPLICATION_DIVISION -> if (value < 0) "÷${-value}" else "×$value"
        else -> if (value >= 0) "+$value" else "−${-value}"
    }

    private fun apply(type: ArithmeticMemoryQuestionType, current: Int, value: Int): Int = when (type) {
        ArithmeticMemoryQuestionType.MULTIPLICATION -> current * max(2, value)
        ArithmeticMemoryQuestionType.MULTIPLICATION_DIVISION -> if (value < 0) current / max(1, -value) else current * value
        else -> current + value
    }
}
