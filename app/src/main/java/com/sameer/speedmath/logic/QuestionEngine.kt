package com.sameer.speedmath.logic

import com.sameer.speedmath.data.SpeedMathRepository
import com.sameer.speedmath.model.*
import java.util.UUID
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

object QuestionEngine {
    fun generate(config: SessionConfig, repository: SpeedMathRepository): List<Question> {
        if (config.mode == SessionMode.ERROR_PRACTICE && repository.errorBank.isNotEmpty()) {
            return repository.errorBank.take(config.questionCount).map { error ->
                val correct = error.correctAnswer
                Question(
                    id = UUID.randomUUID().toString(), moduleId = error.moduleId, moduleTitle = "Error Practice",
                    text = error.questionText, correctAnswer = correct, options = optionsFor(correct), subSkillId = error.subSkillId,
                    explanation = "Repeated from your mistake bank. Last wrong answer: ${error.lastWrongAnswer}."
                )
            }
        }
        return (0 until config.questionCount).map { index -> generateOne(config, repository, index) }
    }

    private fun generateOne(config: SessionConfig, repository: SpeedMathRepository, index: Int): Question {
        val moduleId = when {
            config.moduleId in setOf("misc_mix", "quick_workout", "basics_workout") && config.selectedModuleIds.isNotEmpty() -> config.selectedModuleIds.random()
            config.moduleId == "quick_workout" -> listOf("table", "square", "cube", "square_root", "cube_root", "di_addition", "trigonometry").random()
            config.moduleId == "basics_workout" -> listOf("addition", "subtraction", "multiplication", "division").random()
            config.mode == SessionMode.WORKOUT -> listOf("multiplication", "addition", "simplification", "percentage").random()
            else -> config.moduleId
        }
        val title = repository.modules.find { it.id == moduleId }?.title ?: config.moduleTitle
        return when (moduleId) {
            "simplification" -> simplification(moduleId, title)
            "series" -> series(moduleId, title)
            "quadratic" -> quadratic(moduleId, title)
            "square" -> square(config, moduleId, title)
            "cube" -> cube(config, moduleId, title)
            "square_root" -> squareRoot(config, moduleId, title)
            "cube_root" -> cubeRoot(config, moduleId, title)
            "table" -> table(config, moduleId, title)
            "trigonometry" -> trig(config, moduleId, title)
            "percentage" -> percentage(config, moduleId, title)
            "fraction" -> fraction(moduleId, title)
            "di_addition" -> diAddition(config, moduleId, title)
            "addition" -> addition(config, moduleId, title)
            "subtraction" -> subtraction(config, moduleId, title)
            "multiplication" -> multiplication(config, moduleId, title)
            "division" -> division(config, moduleId, title)
            "complexity" -> complexity(config, moduleId, title)
            else -> mixed(config, index)
        }
    }

    fun isCorrect(question: Question, answer: String): Boolean {
        if (answer.isBlank()) return false
        return when (question.answerType) {
            AnswerType.EXACT_INT, AnswerType.TEXT -> answer.trim().equals(question.correctAnswer.trim(), ignoreCase = true)
            AnswerType.APPROX_INT -> answer.toIntOrNull()?.let { abs(it - question.correctAnswer.toInt()) <= question.tolerance } == true
            AnswerType.DECIMAL -> answer.toDoubleOrNull()?.let { abs(it - question.correctAnswer.toDouble()) <= 0.01 } == true
        }
    }

    fun optionsFor(correct: String, answerType: AnswerType = AnswerType.EXACT_INT): List<String> {
        if (answerType == AnswerType.TEXT) return listOf(correct, "0", "1", "-1").distinct().shuffled().take(4)
        val value = correct.toIntOrNull() ?: return listOf(correct, "0", "1", "2").distinct().shuffled().take(4)
        val deltas = listOf(-12, -8, -5, -3, 2, 4, 7, 11, 15, 20).shuffled()
        val options = mutableSetOf(value)
        deltas.forEach { if (options.size < 4) options.add(value + it) }
        return options.map { it.toString() }.shuffled()
    }

    private fun baseRange(config: SessionConfig): IntRange = when (config.difficulty) {
        Difficulty.EASY -> 10..99
        Difficulty.MEDIUM -> 50..299
        Difficulty.HARD -> 100..999
    }

    private fun addition(config: SessionConfig, moduleId: String, title: String): Question {
        val r = baseRange(config); val count = if (config.difficulty == Difficulty.HARD) 3 else 2
        val nums = List(count) { Random.nextInt(r.first, r.last) }
        val correct = nums.sum()
        return q(moduleId, title, nums.joinToString(" + ") + " = ?", correct.toString(), "${count}_number_addition")
    }

    private fun subtraction(config: SessionConfig, moduleId: String, title: String): Question {
        val r = baseRange(config); val a = Random.nextInt(r.first + 50, r.last + 100); val b = Random.nextInt(r.first, a)
        return q(moduleId, title, "$a - $b = ?", (a - b).toString(), if ((a % 10) < (b % 10)) "borrow_subtraction" else "normal_subtraction")
    }

    private fun multiplication(config: SessionConfig, moduleId: String, title: String): Question {
        val range = when (config.difficulty) { Difficulty.EASY -> 12..99; Difficulty.MEDIUM -> 25..199; Difficulty.HARD -> 80..999 }
        val a = Random.nextInt(range.first, range.last); val b = Random.nextInt(12, if (config.difficulty == Difficulty.HARD) 199 else 99)
        val sub = if (a < 100 && b < 100) "2_digit_by_2_digit" else "mixed_multiplication"
        return q(moduleId, title, "$a × $b = ?", (a * b).toString(), sub)
    }

    private fun division(config: SessionConfig, moduleId: String, title: String): Question {
        val b = Random.nextInt(2, if (config.difficulty == Difficulty.HARD) 35 else 15)
        val ans = Random.nextInt(2, if (config.difficulty == Difficulty.HARD) 80 else 30)
        return q(moduleId, title, "${b * ans} ÷ $b = ?", ans.toString(), "exact_division")
    }

    private fun square(config: SessionConfig, moduleId: String, title: String): Question {
        val n = pick(config, 1, 40); return q(moduleId, title, "$n² = ?", (n * n).toString(), "square_recall")
    }

    private fun cube(config: SessionConfig, moduleId: String, title: String): Question {
        val n = pick(config, 1, 20); return q(moduleId, title, "$n³ = ?", (n * n * n).toString(), "cube_recall")
    }

    private fun squareRoot(config: SessionConfig, moduleId: String, title: String): Question {
        val n = pick(config, 1, 40); return q(moduleId, title, "√${n * n} = ?", n.toString(), "square_root_recall")
    }

    private fun cubeRoot(config: SessionConfig, moduleId: String, title: String): Question {
        val n = pick(config, 1, 20); return q(moduleId, title, "∛${n * n * n} = ?", n.toString(), "cube_root_recall")
    }

    private fun table(config: SessionConfig, moduleId: String, title: String): Question {
        val a = pick(config, 2, 25); val b = Random.nextInt(2, 21); return q(moduleId, title, "$a × $b = ?", (a * b).toString(), "table_recall")
    }

    private fun trig(config: SessionConfig, moduleId: String, title: String): Question {
        val simple = listOf(0, 30, 45, 60, 90)
        val standard = listOf(0, 30, 45, 60, 90, 120, 135, 150, 180, 210, 225, 240, 270, 300, 315, 330, 360)
        val angles = when (config.angleLevel) { "Bigger angles" -> standard.map { it + 360 }; "Standard angles" -> standard; else -> simple }
        val fn = listOf("sin", "cos", "tan").random(); val angle = angles.random(); val norm = ((angle % 360) + 360) % 360
        val value = trigValue(fn, norm)
        return Question(UUID.randomUUID().toString(), moduleId, title, "$fn $angle° = ?", value, listOf(value, "0", "1", "-1", "√3/2", "1/2").distinct().shuffled().take(4), AnswerType.TEXT, subSkillId = "trig_${config.angleLevel}")
    }

    private fun trigValue(fn: String, a: Int): String = when (fn to a) {
        "sin" to 0, "sin" to 180, "sin" to 360 -> "0"
        "sin" to 30, "sin" to 150 -> "1/2"
        "sin" to 45, "sin" to 135 -> "√2/2"
        "sin" to 60, "sin" to 120 -> "√3/2"
        "sin" to 90 -> "1"
        "sin" to 210, "sin" to 330 -> "-1/2"
        "sin" to 225, "sin" to 315 -> "-√2/2"
        "sin" to 240, "sin" to 300 -> "-√3/2"
        "sin" to 270 -> "-1"
        "cos" to 0, "cos" to 360 -> "1"
        "cos" to 30, "cos" to 330 -> "√3/2"
        "cos" to 45, "cos" to 315 -> "√2/2"
        "cos" to 60, "cos" to 300 -> "1/2"
        "cos" to 90, "cos" to 270 -> "0"
        "cos" to 120, "cos" to 240 -> "-1/2"
        "cos" to 135, "cos" to 225 -> "-√2/2"
        "cos" to 150, "cos" to 210 -> "-√3/2"
        "cos" to 180 -> "-1"
        "tan" to 0, "tan" to 180, "tan" to 360 -> "0"
        "tan" to 30, "tan" to 210 -> "1/√3"
        "tan" to 45, "tan" to 225 -> "1"
        "tan" to 60, "tan" to 240 -> "√3"
        "tan" to 120, "tan" to 300 -> "-√3"
        "tan" to 135, "tan" to 315 -> "-1"
        "tan" to 150, "tan" to 330 -> "-1/√3"
        else -> "Undefined"
    }

    private fun percentage(config: SessionConfig, moduleId: String, title: String): Question {
        val types = config.percentageTypes.ifEmpty { setOf("A", "B") }
        return if (types.random() == "A") {
            val a = listOf(5, 10, 12, 20, 25, 33, 50, 75).random(); val b = Random.nextInt(40, 800); val ans = (a * b / 100.0).roundToInt()
            Question(UUID.randomUUID().toString(), moduleId, title, "$a% of $b ≈ ?", ans.toString(), optionsFor(ans.toString()), AnswerType.APPROX_INT, tolerance = 1, subSkillId = "percentage_of_number")
        } else {
            val denominator = Random.nextInt(900, 2500); val numerator = Random.nextInt((denominator * .45).toInt(), denominator); val ans = (numerator * 100.0 / denominator).roundToInt()
            Question(UUID.randomUUID().toString(), moduleId, title, "$numerator / $denominator of 100 ≈ ?", ans.toString(), optionsFor(ans.toString()), AnswerType.APPROX_INT, tolerance = 1, subSkillId = "ratio_to_percentage")
        }
    }

    private fun fraction(moduleId: String, title: String): Question {
        val pairs = listOf("1/2" to "50", "1/3" to "33", "1/4" to "25", "1/5" to "20", "1/8" to "12", "3/4" to "75", "5/8" to "62", "7/8" to "87")
        val p = pairs.random(); return Question(UUID.randomUUID().toString(), moduleId, title, "${p.first} as percentage ≈ ?", p.second, optionsFor(p.second), AnswerType.APPROX_INT, 1, "fraction_percentage")
    }

    private fun diAddition(config: SessionConfig, moduleId: String, title: String): Question {
        val nums = List(config.numbersToAdd.coerceIn(2, 6)) { Random.nextInt(config.from, config.to.coerceAtLeast(config.from + 1)) }
        val ans = nums.sum(); return q(moduleId, title, nums.joinToString(" + ") + " = ?", ans.toString(), "di_${nums.size}_number_addition")
    }

    private fun simplification(moduleId: String, title: String): Question {
        val templates = listOf(
            Triple("36 + 72 + 11 1/9% of 90 + 64 = ?", "182", "11 1/9% of 90 = 10, then add the parts."),
            Triple("24 × 5 + 18 × 3 = ?", "174", "Break into two products and add."),
            Triple("125 + 25% of 280 - 40 = ?", "155", "25% of 280 = 70."),
            Triple("64 + 15 × 8 - 19 = ?", "165", "15 × 8 = 120.")
        ).random()
        return Question(UUID.randomUUID().toString(), moduleId, title, templates.first, templates.second, optionsFor(templates.second), AnswerType.EXACT_INT, subSkillId = "mixed_simplification", explanation = templates.third)
    }

    private fun series(moduleId: String, title: String): Question {
        val start = Random.nextInt(2, 20); val diff = Random.nextInt(3, 12)
        val nums = List(4) { start + it * diff }; val ans = start + 4 * diff
        return q(moduleId, title, nums.joinToString(", ") + ", ?", ans.toString(), "arithmetic_series")
    }

    private fun quadratic(moduleId: String, title: String): Question {
        val r1 = Random.nextInt(2, 10); val r2 = Random.nextInt(2, 10); val sum = r1 + r2; val product = r1 * r2
        return q(moduleId, title, "If x² - ${sum}x + $product = 0, find one positive root", r1.toString(), "factorable_quadratic")
    }

    private fun complexity(config: SessionConfig, moduleId: String, title: String): Question {
        val a = Random.nextInt(config.aMin, config.aMax.coerceAtLeast(config.aMin + 1)); val b = Random.nextInt(config.bMin, config.bMax.coerceAtLeast(config.bMin + 1))
        val ans = when (config.operator) { "+" -> a + b; "-" -> a - b; "÷" -> if (b == 0) 0 else a / b; else -> a * b }
        return q(moduleId, title, "$a ${config.operator} $b = ?", ans.toString(), "custom_${config.operator}")
    }

    private fun mixed(config: SessionConfig, index: Int): Question = when (index % 4) {
        0 -> subtraction(config, "mixed_question", "Mixed Question")
        1 -> addition(config, "mixed_question", "Mixed Question")
        2 -> multiplication(config, "mixed_question", "Mixed Question")
        else -> simplification("mixed_question", "Mixed Question")
    }

    private fun pick(config: SessionConfig, defFrom: Int, defTo: Int): Int {
        val from = if (config.questionMode == QuestionMode.NUMBER_RANGE) config.from else defFrom
        val to = if (config.questionMode == QuestionMode.NUMBER_RANGE) config.to else defTo
        return Random.nextInt(from.coerceAtLeast(1), to.coerceAtLeast(from + 1) + 1)
    }

    private fun q(moduleId: String, title: String, text: String, correct: String, sub: String): Question =
        Question(UUID.randomUUID().toString(), moduleId, title, text, correct, optionsFor(correct), AnswerType.EXACT_INT, subSkillId = sub)
}
