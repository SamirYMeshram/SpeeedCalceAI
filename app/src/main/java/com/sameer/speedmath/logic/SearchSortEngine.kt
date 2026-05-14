package com.sameer.speedmath.logic

import com.sameer.speedmath.model.ArithmeticMemoryRoundResult
import com.sameer.speedmath.model.ArithmeticMemorySort
import com.sameer.speedmath.model.RevisionRecord
import com.sameer.speedmath.model.RevisionSort

/**
 * Manual stable search/sort utilities. They intentionally avoid third-party search,
 * diffing, or database wrapper libraries. UI screens call repository methods, and
 * repository methods delegate here after SQLite returns records.
 */
object SearchSortEngine {
    fun normalize(value: String): String = value.trim().lowercase()
        .replace("²", "2")
        .replace("³", "3")
        .replace("√", "root")
        .replace("∛", "cube root")
        .replace("×", "x")
        .replace("−", "-")
        .replace("÷", "/")
        .replace(Regex("\\s+"), " ")

    fun revision(records: List<RevisionRecord>, query: String, sort: RevisionSort): List<RevisionRecord> {
        val q = normalize(query)
        val scored = records.mapIndexedNotNull { index, record ->
            val score = if (q.isBlank()) 0 else revisionScore(record, q)
            if (q.isBlank() || score > 0) IndexedScore(index, score, record) else null
        }
        val comparator: Comparator<IndexedScore<RevisionRecord>> = when (sort) {
            RevisionSort.DEFAULT_ORDER -> compareByDescending<IndexedScore<RevisionRecord>> { it.score }.thenBy { it.value.orderIndex }.thenBy { it.index }
            RevisionSort.TITLE_ASC -> compareBy<IndexedScore<RevisionRecord>> { normalize(it.value.leftText) }.thenBy { it.index }
            RevisionSort.TITLE_DESC -> compareByDescending<IndexedScore<RevisionRecord>> { normalize(it.value.leftText) }.thenBy { it.index }
            RevisionSort.CATEGORY_ASC -> compareBy<IndexedScore<RevisionRecord>> { it.value.category }.thenBy { it.value.orderIndex }.thenBy { it.index }
            RevisionSort.MASTERY_HIGH -> compareByDescending<IndexedScore<RevisionRecord>> { it.value.mastery }.thenBy { it.value.orderIndex }.thenBy { it.index }
            RevisionSort.MASTERY_LOW -> compareBy<IndexedScore<RevisionRecord>> { it.value.mastery }.thenBy { it.value.orderIndex }.thenBy { it.index }
            RevisionSort.RECENT -> compareByDescending<IndexedScore<RevisionRecord>> { it.value.updatedAt }.thenBy { it.index }
        }
        return stableMergeSort(scored, comparator).map { it.value }
    }

    fun arithmetic(rounds: List<ArithmeticMemoryRoundResult>, query: String, sort: ArithmeticMemorySort): List<ArithmeticMemoryRoundResult> {
        val q = normalize(query)
        val scored = rounds.mapIndexedNotNull { index, round ->
            val score = if (q.isBlank()) 0 else arithmeticScore(round, q)
            if (q.isBlank() || score > 0) IndexedScore(index, score, round) else null
        }
        val comparator: Comparator<IndexedScore<ArithmeticMemoryRoundResult>> = when (sort) {
            ArithmeticMemorySort.NEWEST -> compareByDescending<IndexedScore<ArithmeticMemoryRoundResult>> { it.value.createdAt }.thenBy { it.index }
            ArithmeticMemorySort.OLDEST -> compareBy<IndexedScore<ArithmeticMemoryRoundResult>> { it.value.createdAt }.thenBy { it.index }
            ArithmeticMemorySort.ACCURACY_HIGH -> compareByDescending<IndexedScore<ArithmeticMemoryRoundResult>> { it.value.accuracy }.thenByDescending { it.value.createdAt }.thenBy { it.index }
            ArithmeticMemorySort.TITLE -> compareBy<IndexedScore<ArithmeticMemoryRoundResult>> { normalize(it.value.title) }.thenBy { it.index }
        }
        return stableMergeSort(scored, comparator).map { it.value }
    }

    private fun revisionScore(record: RevisionRecord, query: String): Int {
        val fields = listOf(record.leftText, record.rightText, record.note, record.topicTitle, record.category).map(::normalize)
        var best = 0
        fields.forEach { field ->
            best = maxOf(best, when {
                field == query -> 1000
                field.startsWith(query) -> 800
                field.contains(query) -> 600
                query.split(' ').all { it.isNotBlank() && field.contains(it) } -> 450
                levenshteinWithin(field.take(40), query, 2) -> 250
                else -> 0
            })
        }
        return best + record.frequency
    }

    private fun arithmeticScore(round: ArithmeticMemoryRoundResult, query: String): Int {
        val fields = listOf(round.title, round.correctTotal.toString(), round.userAnswer, round.config.questionType.label).map(::normalize)
        return fields.maxOf { field ->
            when {
                field == query -> 1000
                field.startsWith(query) -> 800
                field.contains(query) -> 600
                else -> 0
            }
        }
    }

    private data class IndexedScore<T>(val index: Int, val score: Int, val value: T)

    private fun <T> stableMergeSort(input: List<T>, comparator: Comparator<T>): List<T> {
        if (input.size <= 1) return input
        val mid = input.size / 2
        val left = stableMergeSort(input.subList(0, mid), comparator)
        val right = stableMergeSort(input.subList(mid, input.size), comparator)
        val out = ArrayList<T>(input.size)
        var i = 0
        var j = 0
        while (i < left.size && j < right.size) {
            if (comparator.compare(left[i], right[j]) <= 0) out.add(left[i++]) else out.add(right[j++])
        }
        while (i < left.size) out.add(left[i++])
        while (j < right.size) out.add(right[j++])
        return out
    }

    private fun levenshteinWithin(a: String, b: String, maxDistance: Int): Boolean {
        if (kotlin.math.abs(a.length - b.length) > maxDistance) return false
        val prev = IntArray(b.length + 1) { it }
        val curr = IntArray(b.length + 1)
        for (i in 1..a.length) {
            curr[0] = i
            var rowMin = curr[0]
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                curr[j] = minOf(prev[j] + 1, curr[j - 1] + 1, prev[j - 1] + cost)
                rowMin = minOf(rowMin, curr[j])
            }
            if (rowMin > maxDistance) return false
            for (j in prev.indices) prev[j] = curr[j]
        }
        return prev[b.length] <= maxDistance
    }
}
