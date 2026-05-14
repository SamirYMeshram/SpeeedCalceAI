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

@Composable
fun DashboardScreen(vm: SpeedMathViewModel, onAttemptHistory: () -> Unit) {
    val s = vm.summary
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { AppHeader(title = "Dashboard", subtitle = "Performance analytics") }
        item { Column(Modifier.padding(horizontal = ScreenPadding)) {
            GlassCard(brush = SmColor.BlueGradient, borderColor = Color.White.copy(alpha = .12f)) {
                Text("Performance Report", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Spacer(Modifier.height(8.dp))
                Text("Speed focus: ${s.focusTopic}. You worked through ${s.weeklyQuestions} attempts with ${s.weeklyAccuracy.oneDecimal()}% accuracy and ${(s.weeklyAvgTimeMs/1000.0).oneDecimal()}s average response time.", color = Color.White.copy(alpha = .88f), fontSize = 13.sp)
            }
        } }
        item { Column(Modifier.padding(horizontal = ScreenPadding)) {
            Text("Performance", color = SmColor.Text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricPill("Today", s.todayQuestions.toString(), SmColor.Primary, Modifier.weight(1f)); MetricPill("Accuracy", s.todayAccuracy.oneDecimal()+"%", SmColor.Green, Modifier.weight(1f)); MetricPill("Avg Time", s.todayAvgTimeMs.secLabel(), SmColor.Orange, Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricPill("Weekly", s.weeklyQuestions.toString(), SmColor.Primary, Modifier.weight(1f)); MetricPill("Accuracy", s.weeklyAccuracy.oneDecimal()+"%", SmColor.Green, Modifier.weight(1f)); MetricPill("Avg Time", s.weeklyAvgTimeMs.secLabel(), SmColor.Orange, Modifier.weight(1f))
            }
        } }
        item { Column(Modifier.padding(horizontal = ScreenPadding)) { DateFilterMock(); SummaryCard(s) } }
        item { Column(Modifier.padding(horizontal = ScreenPadding)) { DonutChartCard("Module-wise Stats", vm.dashboardModuleDistribution()) } }
        item { Column(Modifier.padding(horizontal = ScreenPadding)) { LineChartCard("Accuracy Over Time", vm.accuracyOverTime(), SmColor.Green) } }
        item { Column(Modifier.padding(horizontal = ScreenPadding)) { LineChartCard("Average Time Taken per questions in a module", vm.averageTimeOverTime(), SmColor.Primary) } }
        item { Column(Modifier.padding(horizontal = ScreenPadding)) { LineChartCard("Number of Questions Attempted per day", vm.questionsPerDay(), SmColor.Orange) } }
        item { Column(Modifier.padding(horizontal = ScreenPadding)) { LineChartCard("Total Duration Practiced per day", vm.durationPerDay(), SmColor.Purple) } }
        item { Column(Modifier.padding(horizontal = ScreenPadding)) { GlassCard(onClick = onAttemptHistory) { Text("Attempt History", color = SmColor.Text, fontWeight = FontWeight.Bold, fontSize = 18.sp); Text("Open detailed quiz, workout and 1v1 attempts", color = SmColor.TextMuted, fontSize = 13.sp); Spacer(Modifier.height(12.dp)); PrimaryButton("Open", Modifier.fillMaxWidth(), onClick = onAttemptHistory) } } }
    }
}

@Composable private fun DateFilterMock() { Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) { listOf("1 Week","1 Month","6 Months","1 Year").forEach { SmallChip(it, selected = it == "1 Month") } } ; Spacer(Modifier.height(10.dp)); Text("Apr 13, 2026 - May 13, 2026", color = SmColor.TextMuted, fontSize = 12.sp); Spacer(Modifier.height(12.dp)) }
@Composable private fun SummaryCard(s: DashboardSummary) { GlassCard { Text("Summary", color = SmColor.Text, fontWeight = FontWeight.Bold, fontSize = 17.sp); KeyValueRow("Total Questions", s.totalQuestions.toString()); KeyValueRow("Correct Answers", s.correct.toString(), SmColor.Green); KeyValueRow("Wrong Answers", s.wrong.toString(), SmColor.Red); KeyValueRow("Skipped Answers", s.skipped.toString(), SmColor.Orange); KeyValueRow("Accuracy", s.accuracy.twoDecimal()+"%", SmColor.Green); KeyValueRow("Avg Time", s.avgTimeMs.secLabel(), SmColor.Primary) } }
