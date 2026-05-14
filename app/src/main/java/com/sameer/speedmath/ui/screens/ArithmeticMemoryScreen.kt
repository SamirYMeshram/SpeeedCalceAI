package com.sameer.speedmath.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameer.speedmath.logic.ArithmeticMemoryEngine
import com.sameer.speedmath.model.ArithmeticMemoryConfig
import com.sameer.speedmath.model.ArithmeticMemoryQuestionType
import com.sameer.speedmath.model.ArithmeticMemoryRoundResult
import com.sameer.speedmath.model.ArithmeticMemorySort
import com.sameer.speedmath.model.ArithmeticMemoryStep
import com.sameer.speedmath.model.Difficulty
import com.sameer.speedmath.ui.components.GhostButton
import com.sameer.speedmath.ui.components.PrimaryButton
import com.sameer.speedmath.ui.components.RoundIcon
import com.sameer.speedmath.ui.components.ScreenPadding
import com.sameer.speedmath.ui.theme.SmColor
import com.sameer.speedmath.viewmodel.SpeedMathViewModel
import kotlinx.coroutines.delay

private enum class ArithmeticMemoryPhase { SETUP, RUNNING, FINAL_INPUT, RESULT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArithmeticMemoryScreen(vm: SpeedMathViewModel, onBack: () -> Unit) {
    var phase by remember { mutableStateOf(ArithmeticMemoryPhase.SETUP) }
    var showSetup by remember { mutableStateOf(true) }
    var config by remember { mutableStateOf(ArithmeticMemoryConfig()) }
    var steps by remember { mutableStateOf(emptyList<ArithmeticMemoryStep>()) }
    var stepIndex by remember { mutableIntStateOf(0) }
    var finalAnswer by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<ArithmeticMemoryRoundResult?>(null) }
    var historyQuery by remember { mutableStateOf("") }
    var historySort by remember { mutableStateOf(ArithmeticMemorySort.NEWEST) }

    fun startRound(nextConfig: ArithmeticMemoryConfig) {
        config = nextConfig
        steps = ArithmeticMemoryEngine.generateSteps(nextConfig)
        stepIndex = 0
        finalAnswer = ""
        result = null
        phase = ArithmeticMemoryPhase.RUNNING
        showSetup = false
    }

    LaunchedEffect(phase, steps, config) {
        if (phase == ArithmeticMemoryPhase.RUNNING && steps.isNotEmpty()) {
            for (i in steps.indices) {
                stepIndex = i
                val progress = if (steps.size <= 1) 0.0 else i.toDouble() / (steps.lastIndex.toDouble())
                val pace = config.startPaceSeconds - ((config.startPaceSeconds - config.fastestSeconds) * progress)
                delay((pace * 1000).toLong())
            }
            phase = ArithmeticMemoryPhase.FINAL_INPUT
        }
    }

    Box(Modifier.fillMaxSize().background(Color(0xFF111D24))) {
        Column(Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 18.dp)) {
            ArithmeticMemoryTopBar(onBack)
            when (phase) {
                ArithmeticMemoryPhase.SETUP -> ArithmeticMemoryLandingCard(
                    onStartSetup = { showSetup = true },
                    query = historyQuery,
                    onQuery = { historyQuery = it },
                    sort = historySort,
                    onSort = { historySort = it },
                    history = vm.arithmeticMemoryHistory(historyQuery, historySort)
                )
                ArithmeticMemoryPhase.RUNNING -> {
                    val paceMillis = currentPaceMillis(config, stepIndex, steps.size)
                    ArithmeticMemoryRunningStep(steps.getOrNull(stepIndex), stepIndex, steps.size, paceMillis)
                }
                ArithmeticMemoryPhase.FINAL_INPUT -> ArithmeticMemoryFinalInput(
                    config = config,
                    answer = finalAnswer,
                    onAnswerChange = { finalAnswer = it.filter { ch -> ch.isDigit() || ch == '-' }.take(8) },
                    onCancel = { phase = ArithmeticMemoryPhase.RESULT; result = ArithmeticMemoryEngine.buildResult(config, steps, "") },
                    onSubmit = {
                        val built = ArithmeticMemoryEngine.buildResult(config, steps, finalAnswer)
                        vm.saveArithmeticMemoryRound(built)
                        result = built
                        phase = ArithmeticMemoryPhase.RESULT
                    }
                )
                ArithmeticMemoryPhase.RESULT -> ArithmeticMemoryResultView(
                    result = result ?: ArithmeticMemoryEngine.buildResult(config, steps, finalAnswer),
                    onChangeSetup = { showSetup = true },
                    onPlayAgain = { startRound(config) }
                )
            }
        }

        if (showSetup) {
            ModalBottomSheet(
                onDismissRequest = { if (phase == ArithmeticMemoryPhase.SETUP) onBack() else showSetup = false },
                containerColor = Color(0xFF202020),
                contentColor = Color.White,
                dragHandle = { Box(Modifier.padding(top = 14.dp).size(width = 46.dp, height = 5.dp).clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = .9f))) }
            ) {
                ArithmeticMemorySetupSheet(
                    initial = config,
                    onCancel = { if (phase == ArithmeticMemoryPhase.SETUP) onBack() else showSetup = false },
                    onStart = { startRound(it) }
                )
            }
        }
    }
}

@Composable
private fun ArithmeticMemoryTopBar(onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("‹", color = Color.White, fontSize = 40.sp, modifier = Modifier.width(42.dp).clickable { onBack() })
        Text(
            "Arithmetic Memory",
            modifier = Modifier.weight(1f),
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp
        )
        Spacer(Modifier.width(42.dp))
    }
}

@Composable
private fun ArithmeticMemoryLandingCard(
    onStartSetup: () -> Unit,
    query: String,
    onQuery: (String) -> Unit,
    sort: ArithmeticMemorySort,
    onSort: (ArithmeticMemorySort) -> Unit,
    history: List<ArithmeticMemoryRoundResult>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 88.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF0F374A), Color(0xFF536577))))
                    .clickable { onStartSetup() }
                    .padding(28.dp)
            ) {
                Text("Arithmetic Memory", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(8.dp))
                Text("Timed running-total memory workout. Watch each step, remember the total, then submit the final answer.", color = Color.White.copy(.86f), fontSize = 16.sp, lineHeight = 24.sp)
                Spacer(Modifier.height(22.dp))
                PrimaryButton("Practice Setup", Modifier.fillMaxWidth(), onClick = onStartSetup)
            }
        }
        item {
            Text("Memory history", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 12.dp))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { onQuery(it.take(40)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search rounds, answer, difficulty") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = SmColor.Primary,
                    unfocusedBorderColor = SmColor.Stroke,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = SmColor.Primary
                )
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MemorySortChip("Newest", sort == ArithmeticMemorySort.NEWEST) { onSort(ArithmeticMemorySort.NEWEST) }
                MemorySortChip("Accuracy", sort == ArithmeticMemorySort.ACCURACY_HIGH) { onSort(ArithmeticMemorySort.ACCURACY_HIGH) }
                MemorySortChip("Title", sort == ArithmeticMemorySort.TITLE) { onSort(ArithmeticMemorySort.TITLE) }
            }
        }
        if (history.isEmpty()) {
            item {
                Text("No stored rounds yet. Start a memory round to create persistent history.", color = Color.White.copy(.7f), fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
            }
        } else {
            items(history.take(12), key = { it.id }) { round ->
                MemoryHistoryCard(round)
            }
        }
    }
}

@Composable
private fun MemorySortChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.clip(RoundedCornerShape(50))
            .background(if (selected) SmColor.Primary else Color.Transparent)
            .border(1.dp, if (selected) SmColor.Primary else Color.White.copy(.45f), RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) { Text(label, color = if (selected) Color(0xFF001018) else Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
}

@Composable
private fun MemoryHistoryCard(round: ArithmeticMemoryRoundResult) {
    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1B2932))
            .border(1.dp, Color.White.copy(.08f), RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(round.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("${round.steps.size} steps • correct total ${round.correctTotal}", color = Color.White.copy(.65f), fontSize = 12.sp)
        }
        Text("${round.accuracy}%", color = if (round.isCorrect) SmColor.Green else SmColor.Red, fontWeight = FontWeight.Black, fontSize = 18.sp)
    }
}

@Composable
private fun ColumnScope.ArithmeticMemoryRunningStep(
    step: ArithmeticMemoryStep?,
    stepIndex: Int,
    stepCount: Int,
    paceMillis: Int
) {
    Spacer(Modifier.weight(1f))
    var targetProgress by remember(stepIndex) { mutableFloatStateOf(0f) }
    LaunchedEffect(stepIndex) {
        targetProgress = 0f
        delay(16)
        targetProgress = 1f
    }
    val progress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = paceMillis.coerceAtLeast(350), easing = LinearEasing),
        label = "memoryStepProgress"
    )
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        MemoryProgressRing(progress = progress, text = step?.display ?: "+0")
    }
    Spacer(Modifier.weight(1.3f))
}

private fun currentPaceMillis(config: ArithmeticMemoryConfig, stepIndex: Int, stepCount: Int): Int {
    if (stepCount <= 1) return (config.startPaceSeconds * 1000).toInt()
    val progress = stepIndex.toDouble() / (stepCount - 1).toDouble()
    val seconds = config.startPaceSeconds - ((config.startPaceSeconds - config.fastestSeconds) * progress)
    return (seconds * 1000).toInt()
}

@Composable
private fun MemoryProgressRing(progress: Float, text: String) {
    Box(Modifier.size(300.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
            val inset = 20.dp.toPx()
            val size = Size(size.width - inset * 2, size.height - inset * 2)
            drawArc(Color(0xFF252525), -100f, 330f, false, topLeft = Offset(inset, inset), size = size, style = stroke)
            drawArc(SmColor.Primary, -100f, 330f * progress.coerceIn(0f, 1f), false, topLeft = Offset(inset, inset), size = size, style = stroke)
            drawCircle(Color(0xFF2D3038), radius = 92.dp.toPx())
        }
        Text(text, color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun ArithmeticMemoryFinalInput(
    config: ArithmeticMemoryConfig,
    answer: String,
    onAnswerChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSubmit: () -> Unit
) {
    Spacer(Modifier.height(220.dp))
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Color(0xFF232323)).padding(24.dp)
    ) {
        Text("What was the final total?", color = Color.White, fontWeight = FontWeight.Black, fontSize = 25.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            "You completed ${config.stepCount} steps on ${config.difficulty.name.lowercase()} with ${config.questionType.label.lowercase()} questions.",
            color = Color.White.copy(.9f),
            fontSize = 18.sp,
            lineHeight = 28.sp
        )
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(
            value = answer,
            onValueChange = onAnswerChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Final answer") },
            placeholder = { Text("Enter the total you remember") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = SmColor.Primary,
                unfocusedBorderColor = SmColor.Primary,
                focusedLabelColor = SmColor.Primary,
                unfocusedLabelColor = SmColor.Primary,
                cursorColor = SmColor.Primary
            )
        )
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            GhostButton("Cancel round", Modifier.weight(1f), onCancel)
            PrimaryButton("Submit answer", Modifier.weight(1f), enabled = answer.isNotBlank(), onClick = onSubmit)
        }
    }
}

@Composable
private fun ArithmeticMemoryResultView(result: ArithmeticMemoryRoundResult, onChangeSetup: () -> Unit, onPlayAgain: () -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 78.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF3A2424), Color(0xFF2B1E1E))))
                    .border(1.dp, Color(0xFF6A3333), RoundedCornerShape(22.dp))
                    .padding(18.dp)
            ) {
                Text("Round Complete", color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp)
                Spacer(Modifier.height(8.dp))
                Text(result.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(10.dp))
                Text("Here is the full step-by-step breakdown of the round.", color = Color.White.copy(.9f), fontSize = 18.sp, lineHeight = 26.sp)
                Spacer(Modifier.height(16.dp))
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFF242424)).border(1.dp, Color.White.copy(.22f), RoundedCornerShape(18.dp)).padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ResultMetric("Your answer", result.userAnswer.ifBlank { "—" }, SmColor.Primary)
                    ResultMetric("Correct answer", result.correctTotal.toString(), SmColor.Green)
                    ResultMetric("Accuracy", "${result.accuracy}%", if (result.isCorrect) SmColor.Green else SmColor.Red)
                }
            }
        }
        item {
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Color(0xFF232323)).border(1.dp, Color.White.copy(.22f), RoundedCornerShape(22.dp)).padding(18.dp)
            ) {
                Text("Step-by-step answer", color = Color.White, fontWeight = FontWeight.Black, fontSize = 27.sp)
                Spacer(Modifier.height(8.dp))
                Text("Each row shows the displayed number and the running total after applying it.", color = Color.White.copy(.9f), fontSize = 18.sp, lineHeight = 27.sp)
            }
        }
        items(result.steps) { step -> MemoryStepRow(step) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                GhostButton("☷  Change setup", Modifier.weight(1f), onChangeSetup)
                PrimaryButton("↻  Play again", Modifier.weight(1f), onClick = onPlayAgain)
            }
        }
    }
}

@Composable
private fun ResultMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(value, color = color, fontSize = 17.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun MemoryStepRow(step: ArithmeticMemoryStep) {
    Row(
        Modifier.fillMaxWidth().height(74.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFF232323)).border(1.dp, Color.White.copy(.18f), RoundedCornerShape(20.dp)).padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF12333A)), contentAlignment = Alignment.Center) {
            Text(step.index.toString(), color = SmColor.Primary, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(18.dp))
        Text(step.display, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            Text("Total", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(step.runningTotal.toString(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 26.sp)
        }
    }
}

@Composable
private fun ArithmeticMemorySetupSheet(initial: ArithmeticMemoryConfig, onCancel: () -> Unit, onStart: (ArithmeticMemoryConfig) -> Unit) {
    var difficulty by remember { mutableStateOf(initial.difficulty) }
    var questionType by remember { mutableStateOf(initial.questionType) }
    val config = ArithmeticMemoryConfig(difficulty, questionType)
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 26.dp, vertical = 26.dp)
    ) {
        Text("Practice Setup", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(8.dp))
        Text("Choose Arithmetic Memory settings and start.", color = Color.White.copy(.88f), fontSize = 16.sp)
        Spacer(Modifier.height(32.dp))
        Text("Difficulty", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Spacer(Modifier.height(10.dp))
        Text("Easy runs slower, Hard gets faster and lasts longer.", color = Color.White.copy(.9f), fontSize = 15.sp)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MemoryChoiceChip("☺  Easy", difficulty == Difficulty.EASY, Modifier.weight(1f)) { difficulty = Difficulty.EASY }
            MemoryChoiceChip("↯  Medium", difficulty == Difficulty.MEDIUM, Modifier.weight(1f)) { difficulty = Difficulty.MEDIUM }
            MemoryChoiceChip("♨  Hard", difficulty == Difficulty.HARD, Modifier.weight(1f)) { difficulty = Difficulty.HARD }
        }
        DashedDivider()
        Text("Question Type", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Spacer(Modifier.height(10.dp))
        Text("Choose plus, minus, multiply, multiply with division, or a mixed round.", color = Color.White.copy(.9f), fontSize = 15.sp, lineHeight = 23.sp)
        Spacer(Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MemoryChoiceChip("+  Addition", questionType == ArithmeticMemoryQuestionType.ADDITION, Modifier.weight(1f)) { questionType = ArithmeticMemoryQuestionType.ADDITION }
                MemoryChoiceChip("−  Subtraction", questionType == ArithmeticMemoryQuestionType.SUBTRACTION, Modifier.weight(1.2f)) { questionType = ArithmeticMemoryQuestionType.SUBTRACTION }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MemoryChoiceChip("↘  Add & Subtract", questionType == ArithmeticMemoryQuestionType.ADD_SUBTRACT, Modifier.weight(1.15f)) { questionType = ArithmeticMemoryQuestionType.ADD_SUBTRACT }
                MemoryChoiceChip("×  Multiplication", questionType == ArithmeticMemoryQuestionType.MULTIPLICATION, Modifier.weight(1f)) { questionType = ArithmeticMemoryQuestionType.MULTIPLICATION }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MemoryChoiceChip("⇄  Multiplication & Division", questionType == ArithmeticMemoryQuestionType.MULTIPLICATION_DIVISION, Modifier.weight(1.45f)) { questionType = ArithmeticMemoryQuestionType.MULTIPLICATION_DIVISION }
                MemoryChoiceChip("✦  Mixed", questionType == ArithmeticMemoryQuestionType.MIXED, Modifier.weight(.75f)) { questionType = ArithmeticMemoryQuestionType.MIXED }
            }
        }
        DashedDivider()
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MemorySetupMetric("Sequence", "${config.stepCount} steps")
            MemorySetupMetric("Start pace", "${config.startPaceSeconds} sec")
            MemorySetupMetric("Fastest", "${config.fastestSeconds} sec")
        }
        Spacer(Modifier.height(26.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            GhostButton("Cancel", Modifier.weight(1f), onCancel)
            PrimaryButton("Start  →", Modifier.weight(1f), onClick = { onStart(config) })
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun MemoryChoiceChip(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier.height(50.dp).clip(RoundedCornerShape(9.dp))
            .background(if (selected) Color(0xFF30303C) else Color.Transparent)
            .border(1.dp, if (selected) Color.Transparent else Color.White.copy(.88f), RoundedCornerShape(9.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
private fun MemorySetupMetric(label: String, value: String) {
    Column {
        Text(label, color = Color.White.copy(.84f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))
        Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
    }
}

@Composable
private fun DashedDivider() {
    Spacer(Modifier.height(28.dp))
    Text("- - - - - - - - - - - - - - - - - - - - - - - - - - - -", color = Color.White.copy(.48f), maxLines = 1)
    Spacer(Modifier.height(28.dp))
}
