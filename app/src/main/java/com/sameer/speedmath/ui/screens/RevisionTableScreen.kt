package com.sameer.speedmath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameer.speedmath.model.RevisionContentType
import com.sameer.speedmath.model.RevisionRecord
import com.sameer.speedmath.model.RevisionSort
import com.sameer.speedmath.ui.components.GlassCard
import com.sameer.speedmath.ui.components.RoundIcon
import com.sameer.speedmath.ui.components.ScreenPadding
import com.sameer.speedmath.ui.components.SmallChip
import com.sameer.speedmath.ui.theme.SmColor
import com.sameer.speedmath.viewmodel.SpeedMathViewModel

@Composable
fun RevisionTableScreen(vm: SpeedMathViewModel, topic: String, onBack: () -> Unit) {
    var show by remember(topic) { mutableStateOf(true) }
    var query by remember(topic) { mutableStateOf("") }
    var sort by remember(topic) { mutableStateOf(RevisionSort.DEFAULT_ORDER) }
    var rangeStart by remember(topic) { mutableIntStateOf(1) }
    val rangeTopic = topic in setOf("square", "cube", "square_root", "cube_root")
    val allRecords = vm.revisionRecords(topic, query, sort)
    val records = if (rangeTopic && query.isBlank()) allRecords.filter { it.orderIndex in rangeStart until rangeStart + 20 } else allRecords
    val title = records.firstOrNull()?.topicTitle ?: topic.toTitle()
    val tableLike = records.firstOrNull()?.contentType in setOf(RevisionContentType.VALUE_TABLE, RevisionContentType.FACT)

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.padding(horizontal = ScreenPadding, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundIcon("‹", onBack)
            Spacer(Modifier.width(12.dp))
            Text(title, color = SmColor.Text, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.weight(1f))
            RoundIcon(if (show) "👁" else "◌") { show = !show }
        }

        SearchSortHeader(
            query = query,
            onQuery = { query = it },
            sort = sort,
            onSort = { sort = it }
        )

        LazyColumn(contentPadding = PaddingValues(horizontal = ScreenPadding, vertical = 8.dp)) {
            if (records.isEmpty()) {
                item { EmptyRevisionState(query) }
            } else if (tableLike) {
                records.chunked(2).forEach { pair ->
                    item(key = pair.joinToString { it.id.toString() }) {
                        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            pair.forEach { row -> RevisionValueCard(row, show, Modifier.weight(1f)) { vm.markRevisionViewed(row.id) } }
                            if (pair.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            } else {
                records.forEach { row ->
                    item(key = row.id) {
                        FormulaRevisionCard(row, show) { vm.markRevisionViewed(row.id) }
                    }
                }
            }
            if (rangeTopic && query.isBlank()) {
                item {
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 28.dp)) {
                        listOf(1, 21, 41, 61, 81).forEach { start ->
                            SmallChip("$start-${start + 19}", selected = rangeStart == start) { rangeStart = start }
                        }
                    }
                }
            } else {
                item { Spacer(Modifier.height(30.dp)) }
            }
        }
    }
}

@Composable
private fun SearchSortHeader(query: String, onQuery: (String) -> Unit, sort: RevisionSort, onSort: (RevisionSort) -> Unit) {
    Column(Modifier.padding(horizontal = ScreenPadding)) {
        OutlinedTextField(
            value = query,
            onValueChange = { onQuery(it.take(40)) },
            placeholder = { Text("Search revision data") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = SmColor.Text,
                unfocusedTextColor = SmColor.Text,
                focusedBorderColor = SmColor.Primary,
                unfocusedBorderColor = SmColor.Stroke,
                focusedContainerColor = SmColor.Surface,
                unfocusedContainerColor = SmColor.Surface,
                cursorColor = SmColor.Primary
            )
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            SortChip("Default", sort == RevisionSort.DEFAULT_ORDER) { onSort(RevisionSort.DEFAULT_ORDER) }
            SortChip("A-Z", sort == RevisionSort.TITLE_ASC) { onSort(RevisionSort.TITLE_ASC) }
            SortChip("Mastery", sort == RevisionSort.MASTERY_HIGH) { onSort(RevisionSort.MASTERY_HIGH) }
        }
    }
}

@Composable
private fun SortChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.clip(RoundedCornerShape(50))
            .background(if (selected) SmColor.Primary else SmColor.Surface3)
            .border(1.dp, if (selected) SmColor.Primary else SmColor.Stroke, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 13.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (selected) SmColor.Background else SmColor.Text, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
private fun RevisionValueCard(row: RevisionRecord, show: Boolean, modifier: Modifier, onViewed: () -> Unit) {
    GlassCard(modifier = modifier.height(68.dp), radius = 18, onClick = onViewed) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(row.leftText, color = SmColor.Text, fontWeight = FontWeight.Bold, fontSize = 17.sp, maxLines = 1)
            Spacer(Modifier.weight(1f))
            Text(if (show) row.rightText else "?", color = SmColor.Primary, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, maxLines = 1)
        }
    }
}

@Composable
private fun FormulaRevisionCard(row: RevisionRecord, show: Boolean, onViewed: () -> Unit) {
    GlassCard(Modifier.fillMaxWidth().padding(vertical = 6.dp), radius = 20, onClick = onViewed) {
        Text(row.leftText, color = SmColor.Text, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
        Spacer(Modifier.height(6.dp))
        Text(if (show) row.rightText else "Tap eye to reveal", color = if (show) SmColor.Primary else SmColor.TextMuted, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        if (row.note.isNotBlank()) Text(row.note, color = SmColor.TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallMeta("Mastery ${row.mastery}%")
            SmallMeta("Seen ${row.frequency}")
        }
    }
}

@Composable
private fun SmallMeta(text: String) {
    Box(Modifier.clip(RoundedCornerShape(50)).background(SmColor.Surface3).padding(horizontal = 9.dp, vertical = 5.dp)) {
        Text(text, color = SmColor.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EmptyRevisionState(query: String) {
    GlassCard(Modifier.fillMaxWidth().padding(top = 16.dp), radius = 22) {
        Text("No revision records", color = SmColor.Text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("No stored revision data matched ${if (query.isBlank()) "this topic" else "\"$query\""}.", color = SmColor.TextMuted, modifier = Modifier.padding(top = 6.dp))
    }
}

private fun String.toTitle(): String = split('_').joinToString(" ") { part ->
    part.replaceFirstChar { c -> c.uppercase() }
}.replace("Ratio & Average", "Ratio & Average").replace("Speed Time Work", "Speed Time Work")
