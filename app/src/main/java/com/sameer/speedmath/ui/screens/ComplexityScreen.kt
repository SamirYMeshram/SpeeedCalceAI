package com.sameer.speedmath.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameer.speedmath.model.*
import com.sameer.speedmath.ui.components.*
import com.sameer.speedmath.ui.theme.SmColor
import com.sameer.speedmath.viewmodel.SpeedMathViewModel

@Composable
fun ComplexityScreen(vm: SpeedMathViewModel, onBack: () -> Unit, onStart: () -> Unit) {
    var aMin by remember { mutableStateOf("2") }; var aMax by remember { mutableStateOf("99") }; var bMin by remember { mutableStateOf("101") }; var bMax by remember { mutableStateOf("199") }; var op by remember { mutableStateOf("×") }; var count by remember { mutableIntStateOf(30) }
    Column(Modifier.fillMaxSize().padding(horizontal = ScreenPadding, vertical = 14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { RoundIcon("‹", onBack); Spacer(Modifier.width(12.dp)); Text("Complexity", color = SmColor.Text, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp) }
        Spacer(Modifier.height(20.dp))
        GlassCard {
            Text("Build custom drill", color = SmColor.Text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(16.dp))
            Text("A Range", color = SmColor.TextMuted); Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { SmallNumField("Min", aMin, { aMin = it }, Modifier.weight(1f)); SmallNumField("Max", aMax, { aMax = it }, Modifier.weight(1f)) }
            Spacer(Modifier.height(12.dp))
            Text("B Range", color = SmColor.TextMuted); Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { SmallNumField("Min", bMin, { bMin = it }, Modifier.weight(1f)); SmallNumField("Max", bMax, { bMax = it }, Modifier.weight(1f)) }
            Spacer(Modifier.height(18.dp))
            Text("Operator", color = SmColor.TextMuted); Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { listOf("+", "-", "×", "÷").forEach { SmallChip(it, selected = op == it) { op = it } } }
            Spacer(Modifier.height(16.dp)); CountSelector(count, { count = it }, 5..60)
            Spacer(Modifier.height(18.dp))
            PrimaryButton("Start", Modifier.fillMaxWidth()) {
                val config = SessionConfig(moduleId = "complexity", moduleTitle = "Complexity", questionCount = count, aMin = aMin.toIntOrNull() ?: 2, aMax = aMax.toIntOrNull() ?: 99, bMin = bMin.toIntOrNull() ?: 101, bMax = bMax.toIntOrNull() ?: 199, operator = op)
                vm.startSession(config)
                onStart()
            }
        }
    }
}

@Composable private fun SmallNumField(label: String, value: String, onValue: (String) -> Unit, modifier: Modifier) { OutlinedTextField(value = value, onValueChange = { onValue(it.filter { c -> c.isDigit() }.take(4)) }, label = { Text(label) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = modifier, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = SmColor.Text, unfocusedTextColor = SmColor.Text, focusedBorderColor = SmColor.Primary, unfocusedBorderColor = SmColor.Stroke, focusedLabelColor = SmColor.Primary, unfocusedLabelColor = SmColor.TextMuted)) }
