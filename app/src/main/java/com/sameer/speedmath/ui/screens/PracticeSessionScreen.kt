package com.sameer.speedmath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameer.speedmath.model.*
import com.sameer.speedmath.ui.components.*
import com.sameer.speedmath.ui.theme.SmColor
import com.sameer.speedmath.viewmodel.SpeedMathViewModel
import kotlinx.coroutines.delay

@Composable
fun PracticeRoute(vm: SpeedMathViewModel, moduleId: String, onBack: () -> Unit, onHistory: () -> Unit) {
    val module = when (moduleId) {
        "error_practice" -> PracticeModule("error_practice", "Error Practice", ModuleCategory.MISC, "✕", SetupType.SIMPLE)
        "workout" -> PracticeModule("workout", "Workout", ModuleCategory.MISC, "⚡", SetupType.SIMPLE)
        "daily" -> PracticeModule("daily", "Daily Quiz", ModuleCategory.MISC, "◆", SetupType.SIMPLE)
        "battle" -> PracticeModule("battle", "1v1 Battle", ModuleCategory.MISC, "⚔", SetupType.SIMPLE)
        else -> vm.module(moduleId)
    }
    var showSetup by remember(moduleId) { mutableStateOf(moduleId !in setOf("workout", "error_practice", "battle", "complexity")) }

    LaunchedEffect(moduleId) {
        when (moduleId) {
            "workout" -> vm.startWorkout()
            "error_practice" -> vm.startErrorPractice()
            "battle" -> vm.startSession(SessionConfig("battle", "1v1 Battle", SessionMode.BATTLE, Difficulty.MEDIUM, 12, totalTimeSeconds = 120))
        }
    }

    val runtime = vm.runtime
    LaunchedEffect(runtime?.id, runtime?.completed) {
        while (runtime != null && runtime.completed.not()) {
            delay(100)
            vm.tick()
        }
    }

    val completed = vm.completionDialog
    if (completed != null) {
        PracticeResultScreen(
            session = completed,
            onBack = { vm.clearCompletion(); onBack() },
            onHistory = { vm.clearCompletion(); onHistory() },
            onPracticeAgain = {
                val old = runtime?.config ?: SessionConfig(completed.moduleId, completed.moduleTitle, completed.mode, questionCount = completed.totalQuestions)
                vm.clearCompletion()
                vm.startSession(old)
            }
        )
        return
    }

    PracticeSessionScreen(vm, module, onBack)
    if (showSetup) {
        ModuleSetupSheet(module = module, onCancel = { showSetup = false; onBack() }) { config ->
            val mode = if (moduleId == "daily") SessionMode.DAILY else SessionMode.PRACTICE
            vm.startSession(config.copy(mode = mode, moduleId = if (moduleId == "daily") "mixed_question" else config.moduleId, moduleTitle = if (moduleId == "daily") "Daily Quiz" else config.moduleTitle))
            showSetup = false
        }
    }
}

@Composable
private fun PracticeSessionScreen(vm: SpeedMathViewModel, module: PracticeModule, onBack: () -> Unit) {
    val runtime = vm.runtime
    val q = runtime?.currentQuestion
    Column(Modifier.fillMaxSize().padding(horizontal = ScreenPadding, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RoundIcon("‹", onBack)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(module.title, color = SmColor.Text, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Text("${(runtime?.currentIndex ?: 0) + 1}/${runtime?.questions?.size ?: 0}", color = SmColor.TextMuted, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            StatCounter("Correct", runtime?.correctCount ?: 0, SmColor.Green)
            StatCounter("Wrong", runtime?.wrongCount ?: 0, SmColor.Red)
            Text(timerLabel(runtime), color = SmColor.Primary, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(26.dp))
        GlassCard(Modifier.fillMaxWidth().height(170.dp), radius = 28) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    q?.text ?: "Configure practice to begin",
                    color = SmColor.Text,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            GhostButton("Skip", Modifier.width(104.dp)) { vm.skipQuestion() }
            Spacer(Modifier.weight(1f))
            AutoSubmitToggle(vm.settings.autoSubmit, vm::toggleAutoSubmit)
            Spacer(Modifier.width(8.dp))
            RoundIcon("⚡")
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Answer", color = SmColor.TextMuted, fontSize = 12.sp)
            Spacer(Modifier.weight(1f))
            InputModeSwitcher(runtime?.inputMode ?: InputMode.KEYPAD, vm::toggleInputMode)
        }
        Spacer(Modifier.height(10.dp))
        if (runtime?.inputMode == InputMode.OPTIONS && q != null) {
            OptionGrid(q.options) { vm.selectOption(it) }
        } else {
            GlassCard(Modifier.fillMaxWidth().height(58.dp), radius = 18) {
                Text(
                    runtime?.typedAnswer?.ifBlank { "Type answer" } ?: "",
                    color = if (runtime?.typedAnswer.isNullOrBlank()) SmColor.TextSubtle else SmColor.Text,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(12.dp))
            NumericKeypad(onTap = vm::appendInput, onBack = vm::backspace)
            Spacer(Modifier.height(10.dp))
            PrimaryButton("Submit", Modifier.fillMaxWidth(), enabled = runtime?.typedAnswer?.isNotBlank() == true, onClick = vm::submitCurrent)
        }
    }
}

@Composable
private fun PracticeResultScreen(
    session: PracticeSession,
    onBack: () -> Unit,
    onHistory: () -> Unit,
    onPracticeAgain: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0B1B25), Color(0xFF172239), Color(0xFF1D1231)))),
        contentPadding = PaddingValues(bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                Modifier.fillMaxWidth().background(SmColor.Background).padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("‹", color = Color.White, fontSize = 40.sp, modifier = Modifier.width(48.dp).clickable { onBack() })
                Text("Result", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.Black, fontSize = 21.sp)
                Spacer(Modifier.width(48.dp))
            }
        }
        item { ResultSummaryCard(session, onPracticeAgain) }
        item { FastTrickCard(session.moduleTitle) }
        item {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 14.dp).clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(.08f)).border(1.dp, Color.White.copy(.22f), RoundedCornerShape(28.dp)).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Question Review", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
                    Text("See each attempt with timing and answer details.", color = Color.White.copy(.82f), fontSize = 13.sp)
                }
                SmallChip("${session.attempts.size} items", selected = false)
            }
        }
        items(session.attempts, key = { it.id }) { attempt ->
            ResultAttemptCard(attempt)
        }
        item {
            Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                GhostButton("Attempt History", Modifier.weight(1f), onHistory)
                PrimaryButton("Practise Again", Modifier.weight(1f), onClick = onPracticeAgain)
            }
        }
    }
}

@Composable
private fun ResultSummaryCard(session: PracticeSession, onPracticeAgain: () -> Unit) {
    val solved = session.totalQuestions
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 14.dp).clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF264C58), Color(0xFF20333E))))
            .border(1.dp, Color.White.copy(.28f), RoundedCornerShape(28.dp)).padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text("Session Result", color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp)
                Text("${session.moduleTitle} summary", color = Color.White.copy(.86f), fontSize = 16.sp)
            }
            Box(Modifier.clip(RoundedCornerShape(18.dp)).background(SmColor.Primary.copy(.12f)).border(1.dp, SmColor.Primary.copy(.35f), RoundedCornerShape(18.dp)).padding(horizontal = 16.dp, vertical = 12.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(solved.toString(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Text("Solved", color = Color.White.copy(.86f), fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        LinearProgressIndicator(
            progress = { if (session.totalQuestions == 0) 0f else session.correctCount.toFloat() / session.totalQuestions.toFloat() },
            modifier = Modifier.fillMaxWidth().height(18.dp).clip(RoundedCornerShape(50)),
            color = SmColor.Green,
            trackColor = SmColor.Red
        )
        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            CompactMetric("Correct", "%02d".format(session.correctCount), SmColor.Green)
            CompactMetric("Wrong", "%02d".format(session.wrongCount), SmColor.Red)
            CompactMetric("Skipped", "%02d".format(session.skippedCount), SmColor.TextSubtle)
            CompactMetric("Accuracy", session.accuracy.oneDecimal() + "%", SmColor.Primary)
        }
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ResultInfoBox("▣", "${session.score.twoDecimal()} /${session.totalQuestions * 2}.0", "Score", Modifier.weight(1f))
            ResultInfoBox("◷", session.totalDurationMs.resultDuration(), "Duration", Modifier.weight(1f))
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GhostButton("⚐  Report Question", Modifier.weight(1f)) { }
            PrimaryButton("⟳  Practise Again", Modifier.weight(1f), onClick = onPracticeAgain)
        }
    }
}

@Composable
private fun CompactMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 18.sp)
    }
}

@Composable
private fun ResultInfoBox(icon: String, value: String, label: String, modifier: Modifier = Modifier) {
    Row(modifier.clip(RoundedCornerShape(20.dp)).background(Color.White.copy(.08f)).border(1.dp, Color.White.copy(.20f), RoundedCornerShape(20.dp)).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 22.sp)
        Spacer(Modifier.width(10.dp))
        Column {
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(label, color = Color.White.copy(.78f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun FastTrickCard(moduleTitle: String) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 14.dp).clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF5A3304), Color(0xFF301F0D))))
            .border(1.dp, Color(0xFFFFA726).copy(.25f), RoundedCornerShape(28.dp)).padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(Color(0xFF946000)), contentAlignment = Alignment.Center) { Text("⚡", color = Color.White) }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Fast Calculation Trick", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
                Text("$moduleTitle • Make 10 / 100", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Look for endings like 7 and 3, 8 and 2, 48 and 52.", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 24.sp)
        Spacer(Modifier.height(14.dp))
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White.copy(.08f)).padding(14.dp)) {
            Text("Complement pair\n28 + 72 + 16 = (28 + 72) + 16 = 116", color = Color.White, fontSize = 16.sp, fontFamily = FontFamily.Serif, lineHeight = 28.sp)
        }
        Spacer(Modifier.height(14.dp))
        Text("▣  See full revision tricks", color = SmColor.Orange, fontWeight = FontWeight.Black, fontSize = 16.sp)
    }
}

@Composable
private fun ResultAttemptCard(attempt: QuestionAttempt) {
    val color = when {
        attempt.isSkipped -> SmColor.Orange
        attempt.isCorrect -> SmColor.Green
        else -> SmColor.Red
    }
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 14.dp).clip(RoundedCornerShape(22.dp))
            .background(color.copy(.18f)).border(1.dp, color.copy(.32f), RoundedCornerShape(22.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(34.dp).clip(CircleShape).background(color.copy(.22f)), contentAlignment = Alignment.Center) {
            Text(if (attempt.isCorrect) "✓" else if (attempt.isSkipped) "–" else "×", color = color, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(attempt.questionText.removeSuffix("?"), color = Color.White, fontFamily = FontFamily.Serif, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            if (attempt.isCorrect || attempt.isSkipped) {
                Text("Attempted : ${attempt.userAnswer}", color = Color.White, fontFamily = FontFamily.Serif, fontSize = 16.sp)
            } else {
                Text("Attempted : ${attempt.userAnswer}        Correct : ${attempt.correctAnswer}", color = Color.White, fontFamily = FontFamily.Serif, fontSize = 15.sp)
            }
        }
        Box(Modifier.clip(RoundedCornerShape(50)).background(Color.White.copy(.12f)).border(1.dp, Color.White.copy(.18f), RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(attempt.timeTakenMs.resultDuration(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

private fun timerLabel(runtime: SessionRuntime?): String {
    runtime ?: return "00:00.00"
    val limit = runtime.config.totalTimeSeconds?.times(1000L)
    return if (limit != null) ((limit - runtime.elapsedMs).coerceAtLeast(0L)).clockLabel() + " remaining" else runtime.elapsedMs.clockLabel()
}

private fun Long.resultDuration(): String {
    val tenths = ((this % 1000) / 100).toInt()
    val totalSeconds = (this / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return if (minutes > 0) "${minutes} m ${seconds}.${tenths} s" else "${seconds}.${tenths} sec"
}
