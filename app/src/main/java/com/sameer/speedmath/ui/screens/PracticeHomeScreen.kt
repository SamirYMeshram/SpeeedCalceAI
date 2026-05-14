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
fun PracticeHomeScreen(vm: SpeedMathViewModel, onModuleClick: (PracticeModule) -> Unit, onArithmeticMemory: () -> Unit) {
    val summary = vm.summary
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp)) {
        item { AppHeader() }
        item {
            Column(Modifier.padding(horizontal = ScreenPadding)) {
                Text("Good Afternoon, Sam", color = SmColor.Text, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text("Train your calculation speed with precision.", color = SmColor.TextMuted, fontSize = 13.sp)
                Spacer(Modifier.height(14.dp))
                GlassCard(brush = SmColor.BlueGradient, borderColor = Color.White.copy(alpha = .14f)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Today's Performance", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        SmallChip("Weekly Report (AI)", selected = true)
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricPill("Attempts", summary.todayQuestions.toString(), SmColor.Primary, Modifier.weight(1f))
                        MetricPill("Accuracy", summary.todayAccuracy.oneDecimal() + "%", SmColor.Green, Modifier.weight(1f))
                        MetricPill("Streak", vm.profile.currentStreak.toString() + "d", SmColor.Orange, Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoTag("Focus: ${summary.focusTopic}", Modifier.fillMaxWidth())
                        InfoTag("Strong: ${summary.strongTopic}", Modifier.fillMaxWidth())
                    }
                }
                Spacer(Modifier.height(14.dp))
                GlassCard(brush = SmColor.CyanGradient, onClick = onArithmeticMemory) {
                    Text("Arithmetic Memory · Focus", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text("Timed mental math workout for memory, speed and accuracy.", color = Color.White.copy(alpha = .82f), fontSize = 13.sp)
                }
            }
        }
        ModuleCategory.entries.forEach { cat ->
            item { SectionTitle(cat.title) }
            twoColumnCards(vm.modules.filter { it.category == cat }, onClick = onModuleClick)
        }
    }
}
