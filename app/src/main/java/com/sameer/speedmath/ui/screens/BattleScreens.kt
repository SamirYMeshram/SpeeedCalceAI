package com.sameer.speedmath.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameer.speedmath.ui.components.*
import com.sameer.speedmath.ui.theme.SmColor
import kotlinx.coroutines.delay

@Composable
fun WorkoutIntroScreen(onBack: () -> Unit, onStart: () -> Unit) {
    InfoSheetScreen(title = "Workout", icon = "🏆", bullets = listOf("Once a quiz begins, it cannot be paused.", "Skipped questions cannot be reattempted.", "You cannot revisit previous questions after submitting an answer."), meta = listOf("Questions" to "20", "Marks" to "40.0", "Total Time" to "2m 30s"), onBack = onBack, onStart = onStart)
}

@Composable
fun BattleIntroScreen(onBack: () -> Unit, onStart: () -> Unit) {
    InfoSheetScreen(title = "1v1 Instructions", icon = "⚔", bullets = listOf("Tap Start to continue.", "Play Online matches you with a random player.", "Play With Friend creates or joins a room code.", "Once match begins, timer runs and cannot be paused.", "Answer quickly and accurately to maximize score."), meta = emptyList(), onBack = onBack, onStart = onStart)
}

@Composable
private fun InfoSheetScreen(title: String, icon: String, bullets: List<String>, meta: List<Pair<String,String>>, onBack: () -> Unit, onStart: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = ScreenPadding, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { RoundIcon("‹", onBack); Spacer(Modifier.width(12.dp)); Text(title, color = SmColor.Text, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp) }
        Spacer(Modifier.height(22.dp))
        GlassCard {
            Box(Modifier.fillMaxWidth().height(110.dp), contentAlignment = Alignment.Center) { Text(icon, fontSize = 62.sp) }
            bullets.forEachIndexed { i, b -> Text("${i+1}. $b", color = SmColor.TextMuted, fontSize = 14.sp, modifier = Modifier.padding(vertical = 5.dp)) }
            if (meta.isNotEmpty()) { Spacer(Modifier.height(12.dp)); meta.forEach { KeyValueRow(it.first, it.second, SmColor.Primary) } }
            Spacer(Modifier.height(18.dp)); Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { GhostButton("Cancel", Modifier.weight(1f), onBack); PrimaryButton("Start", Modifier.weight(1f), onClick = onStart) }
        }
    }
}

@Composable
fun BattleModeScreen(onBack: () -> Unit, onOnline: () -> Unit, onFriend: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = ScreenPadding, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { RoundIcon("‹", onBack); Spacer(Modifier.width(12.dp)); Text("Choose 1v1 Mode", color = SmColor.Text, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp) }
        Spacer(Modifier.height(22.dp))
        GlassCard(onClick = onOnline) { Text("Play Online", color = SmColor.Text, fontWeight = FontWeight.Bold, fontSize = 18.sp); Text("Match with random players", color = SmColor.TextMuted) }
        Spacer(Modifier.height(12.dp))
        GlassCard(onClick = onFriend) { Text("Play With Friend", color = SmColor.Text, fontWeight = FontWeight.Bold, fontSize = 18.sp); Text("Create or join by room code", color = SmColor.TextMuted) }
    }
}

@Composable
fun BattleCountdownScreen(onBack: () -> Unit, onStartBattle: () -> Unit) {
    var count by remember { mutableIntStateOf(3) }
    LaunchedEffect(Unit) { while (count > 0) { delay(900); count-- }; onStartBattle() }
    Column(Modifier.fillMaxSize().padding(horizontal = ScreenPadding, vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { RoundIcon("‹", onBack); Spacer(Modifier.width(12.dp)); Text("1v1 Battle", color = SmColor.Text, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp) }
        Spacer(Modifier.height(36.dp))
        SmallChip("Match Found", selected = true)
        Spacer(Modifier.height(12.dp))
        Text("You vs 5253 Rahul More", color = SmColor.Text, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, textAlign = TextAlign.Center)
        Text("Same questions. Same timer. Fastest mind wins.", color = SmColor.TextMuted, textAlign = TextAlign.Center)
        Spacer(Modifier.height(28.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) { PlayerCard("You", "Sam Meshram", Modifier.weight(1f)); PlayerCard("Rival", "5253 Rahul More", Modifier.weight(1f)) }
        Spacer(Modifier.height(48.dp))
        androidx.compose.foundation.layout.Box(Modifier.size(120.dp).clip(CircleShape), contentAlignment = Alignment.Center) { Text(count.coerceAtLeast(1).toString(), color = SmColor.Primary, fontWeight = FontWeight.Black, fontSize = 58.sp) }
        Text("Starting in", color = SmColor.TextMuted)
    }
}

@Composable private fun PlayerCard(label: String, name: String, modifier: Modifier) { GlassCard(modifier = modifier.height(120.dp), radius = 24) { Text(label, color = SmColor.Primary, fontWeight = FontWeight.Bold); Spacer(Modifier.height(10.dp)); Text(name, color = SmColor.Text, fontWeight = FontWeight.Bold, fontSize = 15.sp) } }
