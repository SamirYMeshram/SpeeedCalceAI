package com.sameer.speedmath.model

enum class RevisionContentType { VALUE_TABLE, FORMULA, TRICK, FACT }

enum class RevisionSort { DEFAULT_ORDER, TITLE_ASC, TITLE_DESC, CATEGORY_ASC, MASTERY_HIGH, MASTERY_LOW, RECENT }

data class RevisionRecord(
    val id: Long = 0L,
    val topicSlug: String,
    val topicTitle: String,
    val category: String,
    val contentType: RevisionContentType,
    val leftText: String,
    val rightText: String,
    val note: String = "",
    val orderIndex: Int = 0,
    val mastery: Int = 0,
    val frequency: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

data class RevisionQuery(
    val topicSlug: String,
    val search: String = "",
    val sort: RevisionSort = RevisionSort.DEFAULT_ORDER,
    val showAnswers: Boolean = true
)
