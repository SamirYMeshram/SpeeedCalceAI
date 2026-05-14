package com.sameer.speedmath.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameer.speedmath.ui.components.*
import com.sameer.speedmath.ui.theme.SmColor
import com.sameer.speedmath.viewmodel.SpeedMathViewModel

@Composable
fun AccountScreen(vm: SpeedMathViewModel, onSettings: () -> Unit) {
    val p = vm.profile
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { AppHeader(title = "Account", subtitle = "Profile and progress") }
        item { Column(Modifier.padding(horizontal = ScreenPadding)) {
            GlassCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.layout.Box(Modifier.size(72.dp).clip(CircleShape).then(Modifier), contentAlignment = Alignment.Center) {
                        Text("SM", color = SmColor.Primary, fontWeight = FontWeight.Black, fontSize = 24.sp)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(p.name, color = SmColor.Text, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text(p.email, color = SmColor.TextMuted, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricPill("30D Rank", p.rank30d, SmColor.Primary, Modifier.weight(1f)); MetricPill("Lifetime", p.lifetimeRank, SmColor.Purple, Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricPill("Level", p.level, SmColor.Green, Modifier.weight(1f)); MetricPill("Points", "${p.points} pts", SmColor.Orange, Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricPill("Current Streak", p.currentStreak.toString(), SmColor.Primary, Modifier.weight(1f)); MetricPill("Best Streak", p.bestStreak.toString(), SmColor.Pink, Modifier.weight(1f))
                }
            }
        } }
        item { SectionTitle("Learning Insights") }
        item { Column(Modifier.padding(horizontal = ScreenPadding)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricPill("Weekly", vm.summary.weeklyQuestions.toString(), SmColor.Primary, Modifier.weight(1f)); MetricPill("Daily Quiz", "1", SmColor.Green, Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricPill("Workout", vm.sessions.count { it.mode.name == "WORKOUT" }.toString(), SmColor.Orange, Modifier.weight(1f)); MetricPill("Battles", "0 / 2", SmColor.Purple, Modifier.weight(1f))
            }
        } }
        item { Column(Modifier.padding(horizontal = ScreenPadding), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AccountRow("Ad-Free", if (p.adFreeActive) "Active" else "Inactive", SmColor.Green) {}
            AccountRow("Active Sessions", "This device") {}
            AccountRow("Settings", "Theme, language, auto submit", onClick = onSettings)
            AccountRow("Report a Problem", "Send issue details") {}
            AccountRow("Sign Out", "Leave this account", SmColor.Red) {}
            Text("Speed Math ID: ${p.speedMathId}", color = SmColor.TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 10.dp))
        } }
    }
}

@Composable private fun AccountRow(title: String, subtitle: String, color: Color = SmColor.Text, onClick: () -> Unit) {
    GlassCard(onClick = onClick, radius = 20) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text(title, color = color, fontWeight = FontWeight.Bold); Text(subtitle, color = SmColor.TextMuted, fontSize = 12.sp) }
            Text("›", color = SmColor.TextMuted, fontSize = 24.sp)
        }
    }
}
