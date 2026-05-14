package com.sameer.speedmath.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameer.speedmath.model.*
import com.sameer.speedmath.ui.components.*
import com.sameer.speedmath.ui.theme.SmColor
import com.sameer.speedmath.viewmodel.SpeedMathViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AttemptHistoryScreen(vm: SpeedMathViewModel, onBack: () -> Unit, onReview: (String) -> Unit) {
    val grouped = vm.sessions.groupBy { dayLabel(it.startedAt) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp)) {
        item { Row(Modifier.padding(horizontal = ScreenPadding, vertical = 14.dp)) { RoundIcon("‹", onBack); Spacer(Modifier.width(12.dp)); Text("Attempt History", color = SmColor.Text, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp) } }
        item { Column(Modifier.padding(horizontal = ScreenPadding)) { SmallChip("Filter by module: All", selected = false) } }
        grouped.forEach { (day, sessions) ->
            item { SectionTitle(day) }
            sessions.forEach { session ->
                item { AttemptCard(session, onReview = { onReview(session.id) }) }
            }
        }
    }
}

@Composable
private fun AttemptCard(session: PracticeSession, onReview: () -> Unit) {
    Column(Modifier.padding(horizontal = ScreenPadding, vertical = 6.dp)) {
        GlassCard(radius = 22) {
            Row { Text(timeLabel(session.startedAt), color = SmColor.TextMuted, fontSize = 12.sp); Spacer(Modifier.weight(1f)); Text(session.moduleTitle, color = SmColor.Primary, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricPill("Score", session.score.oneDecimal(), SmColor.Orange, Modifier.weight(1f)); MetricPill("Accuracy", session.accuracy.oneDecimal()+"%", SmColor.Green, Modifier.weight(1f)); MetricPill("Time", session.totalDurationMs.shortDurationLabel(), SmColor.Primary, Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { SmallChip("✓ ${session.correctCount}"); SmallChip("Skip ${session.skippedCount}"); SmallChip("✕ ${session.wrongCount}") }
            Spacer(Modifier.height(12.dp))
            PrimaryButton("Review", Modifier.fillMaxWidth(), onClick = onReview)
        }
    }
}

@Composable
fun AttemptReviewScreen(vm: SpeedMathViewModel, sessionId: String?, onBack: () -> Unit) {
    val session = vm.session(sessionId)
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp)) {
        item { Row(Modifier.padding(horizontal = ScreenPadding, vertical = 14.dp)) { RoundIcon("‹", onBack); Spacer(Modifier.width(12.dp)); Text(session?.let { "${it.moduleTitle} - ${dayLabel(it.startedAt)}" } ?: "Attempt Review", color = SmColor.Text, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp) } }
        if (session == null) item { Text("Session not found", color = SmColor.TextMuted, modifier = Modifier.padding(ScreenPadding)) } else {
            item { Column(Modifier.padding(horizontal = ScreenPadding)) { GlassCard { Text("Attempt Summary", color = SmColor.Text, fontWeight = FontWeight.Bold, fontSize = 18.sp); KeyValueRow("Total", session.totalQuestions.toString()); KeyValueRow("Correct", session.correctCount.toString(), SmColor.Green); KeyValueRow("Wrong", session.wrongCount.toString(), SmColor.Red); KeyValueRow("Skipped", session.skippedCount.toString(), SmColor.Orange); KeyValueRow("Accuracy", session.accuracy.oneDecimal()+"%", SmColor.Green); KeyValueRow("Time", session.totalDurationMs.shortDurationLabel(), SmColor.Primary) } } }
            session.attempts.forEachIndexed { index, attempt -> item { QuestionReviewCard(index + 1, attempt) } }
        }
    }
}

@Composable private fun QuestionReviewCard(index: Int, a: QuestionAttempt) {
    val color = when { a.isSkipped -> SmColor.Orange; a.isCorrect -> SmColor.Green; else -> SmColor.Red }
    Column(Modifier.padding(horizontal = ScreenPadding, vertical = 6.dp)) {
        GlassCard(borderColor = color.copy(alpha = .65f), radius = 22) {
            Row { Text("Q$index", color = color, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); SmallChip(if (a.isSkipped) "Skipped" else if (a.isCorrect) "Correct" else "Wrong") }
            Spacer(Modifier.height(8.dp))
            Text(a.questionText, color = SmColor.Text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            KeyValueRow("Attempted", a.userAnswer, if (a.isCorrect) SmColor.Green else SmColor.Red)
            if (!a.isCorrect) KeyValueRow("Correct", a.correctAnswer, SmColor.Green)
            KeyValueRow("Time", a.timeTakenMs.secLabel(), SmColor.Primary)
        }
    }
}

private fun dayLabel(ms: Long): String = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(ms))
private fun timeLabel(ms: Long): String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(ms))
