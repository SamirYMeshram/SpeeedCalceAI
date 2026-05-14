package com.sameer.speedmath.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameer.speedmath.ui.components.*
import com.sameer.speedmath.ui.theme.SmColor
import com.sameer.speedmath.viewmodel.SpeedMathViewModel

@Composable
fun ChallengeScreen(vm: SpeedMathViewModel, onStartError: () -> Unit, onWorkout: () -> Unit, onBattle: () -> Unit, onDaily: () -> Unit) {
    androidx.compose.foundation.lazy.LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp)) {
        item { AppHeader(title = "Quiz", subtitle = "Benchmarks and pressure modes") }
        item {
            Column(Modifier.padding(horizontal = ScreenPadding), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                GlassCard(brush = SmColor.ChallengeGradient, borderColor = Color.White.copy(alpha = .12f)) {
                    Text("Daily Quiz", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Today · 13 May", color = Color.White.copy(alpha = .8f), fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Test yourself against today's competition. Solve today's set to unlock marks, rank and percentile.", color = Color.White.copy(alpha = .86f), fontSize = 13.sp)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { GhostButton("Result", Modifier.weight(1f)) {}; PrimaryButton("Start", Modifier.weight(1f), onClick = onDaily) }
                }
                GlassCard(brush = SmColor.BattleGradient, borderColor = Color.White.copy(alpha = .13f)) {
                    Text("1V1 Arena", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Same questions. Same timer. Fastest mind wins.", color = Color.White.copy(alpha = .84f), fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricPill("Wins", "0", SmColor.Green, Modifier.weight(1f)); MetricPill("Battles", "2", SmColor.Primary, Modifier.weight(1f)); MetricPill("Win Rate", "0%", SmColor.Orange, Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { SmallChip("Live", selected = true); SmallChip("Friends") }
                    Spacer(Modifier.height(12.dp))
                    PrimaryButton("Start 1V1", Modifier.fillMaxWidth(), onClick = onBattle)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SmallChallengeCard("Error Practice", "Repeat your mistake bank", "✕", SmColor.Red, Modifier.weight(1f), onStartError)
                    SmallChallengeCard("Workout", "Strict timed drill", "⚡", SmColor.Purple, Modifier.weight(1f), onWorkout)
                }
            }
        }
    }
}

@Composable
private fun SmallChallengeCard(title: String, subtitle: String, icon: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    GlassCard(modifier = modifier.height(156.dp), radius = 24, onClick = onClick) {
        Text(icon, fontSize = 28.sp, color = color)
        Spacer(Modifier.height(10.dp))
        Text(title, color = SmColor.Text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(subtitle, color = SmColor.TextMuted, fontSize = 12.sp)
    }
}
