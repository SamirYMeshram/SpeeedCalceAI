package com.sameer.speedmath.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sameer.speedmath.ui.components.*

@Composable
fun RevisionScreen(onOpenTable: (String) -> Unit) {
    val basics = listOf("Square" to "²", "Cube" to "³", "Table" to "×", "Trigonometry" to "△", "Fraction" to "½", "Pythagorean Triplets" to "⊿")
    val formula = listOf("Mensuration 2D" to "□", "Mensuration 3D" to "▣", "Arithmetic" to "∑", "Ratio & Average" to "∷", "Speed Time Work" to "⏱", "Trigonometry" to "△", "Algebra" to "x", "Geometry" to "◇")
    val calc = listOf("Addition" to "+", "Subtraction" to "−", "Multiplication" to "×", "Division" to "÷", "Percentage" to "%", "Powers & Roots" to "√", "Simplification" to "∑", "Exam Tricks" to "⚡")
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp)) {
        item { AppHeader(title = "Revision", subtitle = "Formulas, values and shortcuts") }
        revisionSection("Basics", basics, onOpenTable)
        revisionSection("Formula", formula, onOpenTable)
        revisionSection("Calculation", calc, onOpenTable)
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.revisionSection(title: String, items: List<Pair<String, String>>, onOpen: (String) -> Unit) {
    item { SectionTitle(title) }
    items.chunked(2).forEach { row ->
        item {
            Row(Modifier.fillMaxWidth().padding(horizontal = ScreenPadding, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { (label, icon) -> RevisionCard(label, icon, Modifier.weight(1f)) { onOpen(label.lowercase().replace(" ", "_")) } }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}
