package com.sameer.speedmath.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameer.speedmath.model.*
import com.sameer.speedmath.ui.components.*
import com.sameer.speedmath.ui.theme.SmColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleSetupSheet(module: PracticeModule, onCancel: () -> Unit, onStart: (SessionConfig) -> Unit) {
    var difficulty by remember { mutableStateOf(Difficulty.EASY) }
    var count by remember { mutableIntStateOf(10) }
    var qMode by remember { mutableStateOf(QuestionMode.NUMBER_RANGE) }
    var from by remember { mutableStateOf("5") }
    var to by remember { mutableStateOf("25") }
    var typeA by remember { mutableStateOf(true) }
    var typeB by remember { mutableStateOf(true) }
    var angle by remember { mutableStateOf("Simple angles") }
    var selected by remember { mutableStateOf(defaultSelected(module.id)) }
    var numbersToAdd by remember { mutableIntStateOf(2) }

    ModalBottomSheet(onDismissRequest = onCancel, containerColor = SmColor.Surface, contentColor = SmColor.Text) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)) {
            Text("${module.title} Practice Setup", color = SmColor.Text, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Spacer(Modifier.height(16.dp))
            when (module.setupType) {
                SetupType.RANGE -> {
                    QuestionModeRow(qMode) { qMode = it }
                    Spacer(Modifier.height(14.dp)); RangeInputs(from, to, { from = it }, { to = it })
                    Spacer(Modifier.height(14.dp)); Text("Difficulty", color = SmColor.Text, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(8.dp)); DifficultySelector(difficulty) { difficulty = it }
                }
                SetupType.TRIG -> { CountSelector(count, { count = it }); Spacer(Modifier.height(14.dp)); Text("Angle level", color = SmColor.Text, fontWeight = FontWeight.SemiBold); listOf("Simple angles", "Standard angles", "Bigger angles").forEach { item -> Row { RadioButton(selected = angle == item, onClick = { angle = item }, colors = RadioButtonDefaults.colors(selectedColor = SmColor.Primary)); Text(item, color = SmColor.Text, modifier = Modifier.padding(top = 12.dp)) } } }
                SetupType.PERCENTAGE -> { QuestionTypeRow("Type A", "a % of b = ?", typeA) { typeA = !typeA }; QuestionTypeRow("Type B", "a / b of 100 = ?", typeB) { typeB = !typeB } }
                SetupType.MULTI_SELECT -> { Text("Select modules", color = SmColor.Text, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(10.dp)); val opts = defaultSelected(module.id) + if (module.id == "misc_mix") emptySet() else setOf("di_addition", "trigonometry"); FlowChips(opts.toList(), selected) { id -> selected = if (selected.contains(id)) selected - id else selected + id }; Spacer(Modifier.height(12.dp)); Text("Difficulty", color = SmColor.Text, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(8.dp)); DifficultySelector(difficulty) { difficulty = it } }
                SetupType.DI_ADDITION -> { QuestionModeRow(qMode) { qMode = it }; Spacer(Modifier.height(14.dp)); RangeInputs(from, to, { from = it }, { to = it }); Spacer(Modifier.height(14.dp)); Text("Numbers To Add", color = SmColor.Text, fontWeight = FontWeight.SemiBold); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { (2..6).forEach { SmallChip(it.toString(), selected = numbersToAdd == it) { numbersToAdd = it } } }; Spacer(Modifier.height(12.dp)); DifficultySelector(difficulty) { difficulty = it } }
                else -> { Text("Difficulty", color = SmColor.Text, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(8.dp)); DifficultySelector(difficulty) { difficulty = it } }
            }
            if (module.setupType !in setOf(SetupType.TRIG)) { Spacer(Modifier.height(16.dp)); CountSelector(count, { count = it }) }
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GhostButton("Cancel", Modifier.weight(1f), onCancel)
                PrimaryButton("Start", Modifier.weight(1f), enabled = selected.isNotEmpty() || module.setupType != SetupType.MULTI_SELECT) {
                    onStart(SessionConfig(
                        moduleId = module.id, moduleTitle = module.title, difficulty = difficulty, questionCount = count, questionMode = qMode,
                        from = from.toIntOrNull() ?: 1, to = to.toIntOrNull() ?: 20,
                        percentageTypes = buildSet { if (typeA) add("A"); if (typeB) add("B") }, selectedModuleIds = selected,
                        angleLevel = angle, numbersToAdd = numbersToAdd
                    ))
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun defaultSelected(moduleId: String): Set<String> = when (moduleId) {
    "misc_mix" -> setOf("simplification", "series", "quadratic")
    "quick_workout" -> setOf("table", "square", "cube", "square_root", "cube_root")
    "basics_workout" -> setOf("addition", "subtraction", "multiplication", "division")
    else -> emptySet()
}

@Composable private fun QuestionModeRow(mode: QuestionMode, onChange: (QuestionMode) -> Unit) { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { SmallChip("Number range", selected = mode == QuestionMode.NUMBER_RANGE) { onChange(QuestionMode.NUMBER_RANGE) }; SmallChip("Random", selected = mode == QuestionMode.RANDOM) { onChange(QuestionMode.RANDOM) } } }
@Composable private fun RangeInputs(from: String, to: String, setFrom: (String) -> Unit, setTo: (String) -> Unit) { Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { NumberField("From", from, setFrom, Modifier.weight(1f)); NumberField("To", to, setTo, Modifier.weight(1f)) } }
@Composable private fun NumberField(label: String, value: String, onValue: (String) -> Unit, modifier: Modifier) { OutlinedTextField(value = value, onValueChange = { onValue(it.filter(Char::isDigit).take(4)) }, label = { Text(label) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = modifier, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = SmColor.Text, unfocusedTextColor = SmColor.Text, focusedBorderColor = SmColor.Primary, unfocusedBorderColor = SmColor.Stroke, focusedLabelColor = SmColor.Primary, unfocusedLabelColor = SmColor.TextMuted)) }
@Composable private fun QuestionTypeRow(title: String, subtitle: String, checked: Boolean, onClick: () -> Unit) { Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) { Column { Text(title, color = SmColor.Text, fontWeight = FontWeight.Bold); Text(subtitle, color = SmColor.TextMuted, fontSize = 12.sp) }; Switch(checked = checked, onCheckedChange = { onClick() }, colors = SwitchDefaults.colors(checkedTrackColor = SmColor.Primary)) } }
@Composable private fun FlowChips(items: List<String>, selected: Set<String>, onToggle: (String) -> Unit) { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { items.chunked(2).forEach { row -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { row.forEach { id -> SmallChip(id.replace('_',' ').replaceFirstChar { it.uppercase() }, selected.contains(id)) { onToggle(id) } } } } } }
