package com.sameer.speedmath.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameer.speedmath.model.*
import com.sameer.speedmath.ui.theme.SmColor

val ScreenPadding = 18.dp
val CardRadius = 24.dp

@Composable
fun ScreenBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SmColor.Background),
        content = content
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    radius: Int = 24,
    brush: Brush? = null,
    borderColor: Color = SmColor.Stroke,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(radius.dp)
    val base = modifier
        .clip(shape)
        .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
        .background(brush ?: Brush.linearGradient(listOf(SmColor.Surface2, SmColor.Surface)))
        .border(1.dp, borderColor.copy(alpha = 0.85f), shape)
        .padding(16.dp)
    Column(base, content = content)
}

@Composable
fun AppHeader(title: String = "Speed Math", subtitle: String = "Personal calculation gym", showActions: Boolean = true) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenPadding, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(SmColor.CyanGradient), contentAlignment = Alignment.Center) {
            Text("∑", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = SmColor.Text)
            Text(subtitle, fontSize = 12.sp, color = SmColor.TextMuted, maxLines = 1)
        }
        if (showActions) {
            RoundIcon("🔔")
            Spacer(Modifier.width(8.dp))
            RoundIcon("⋯")
        }
    }
}

@Composable
fun RoundIcon(text: String, onClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier.size(38.dp).clip(CircleShape)
            .background(SmColor.Surface2)
            .border(1.dp, SmColor.Stroke, CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) { Text(text, fontSize = 15.sp, color = SmColor.Text) }
}

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(title, modifier = modifier.padding(horizontal = ScreenPadding, vertical = 12.dp), color = SmColor.Text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
}

@Composable
fun ModuleCard(module: PracticeModule, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(22.dp)
    Row(
        modifier = modifier
            .height(88.dp)
            .clip(shape)
            .background(Brush.linearGradient(listOf(SmColor.Surface2, SmColor.Surface)))
            .border(1.dp, SmColor.Stroke, shape)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(34.dp).clip(RoundedCornerShape(12.dp)).background(SmColor.Surface3),
            contentAlignment = Alignment.Center
        ) {
            Text(module.icon, fontWeight = FontWeight.Bold, color = SmColor.Primary, fontSize = 18.sp)
        }
        Spacer(Modifier.width(8.dp))
        Text(
            module.title,
            Modifier.weight(1f),
            color = SmColor.Text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.5.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 15.sp
        )
        Text("›", color = SmColor.TextMuted, fontSize = 20.sp)
    }
}

@Composable
fun RevisionCard(title: String, icon: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(22.dp)
    Row(
        modifier = modifier
            .height(86.dp)
            .clip(shape)
            .background(Brush.linearGradient(listOf(SmColor.Surface2, SmColor.Surface)))
            .border(1.dp, SmColor.Stroke, shape)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(34.dp).clip(RoundedCornerShape(12.dp)).background(SmColor.Surface3), contentAlignment = Alignment.Center) {
            Text(icon, color = SmColor.Primary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.width(8.dp))
        Text(title, Modifier.weight(1f), color = SmColor.Text, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 15.sp)
        Text("›", color = SmColor.TextMuted, fontSize = 20.sp)
    }
}

@Composable
fun MetricPill(label: String, value: String, color: Color = SmColor.Primary, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(18.dp)).background(SmColor.Surface.copy(alpha = 0.72f)).border(1.dp, SmColor.Stroke, RoundedCornerShape(18.dp)).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        Text(label, color = SmColor.TextMuted, fontSize = 11.sp)
    }
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SmColor.Primary, contentColor = Color(0xFF001018), disabledContainerColor = SmColor.Surface3)
    ) { Text(text, fontWeight = FontWeight.Bold) }
}

@Composable
fun GhostButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, SmColor.Stroke),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = SmColor.Text)
    ) { Text(text, fontWeight = FontWeight.Bold) }
}

@Composable
fun DifficultySelector(selected: Difficulty, onSelected: (Difficulty) -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(SmColor.Surface).padding(4.dp)) {
        Difficulty.entries.forEach { diff ->
            val active = diff == selected
            Box(
                Modifier.weight(1f).height(42.dp).clip(RoundedCornerShape(14.dp))
                    .background(if (active) SmColor.Primary else Color.Transparent)
                    .clickable { onSelected(diff) },
                contentAlignment = Alignment.Center
            ) {
                Text(diff.name.lowercase().replaceFirstChar { it.uppercase() }, color = if (active) Color(0xFF001018) else SmColor.TextMuted, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun CountSelector(count: Int, onChange: (Int) -> Unit, range: IntRange = 5..50) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Number of Questions", color = SmColor.Text, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            Text(count.toString(), color = SmColor.Primary, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = count.toFloat(),
            onValueChange = { onChange(it.toInt().coerceIn(range)) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.count() - 2,
            colors = SliderDefaults.colors(thumbColor = SmColor.Primary, activeTrackColor = SmColor.Primary, inactiveTrackColor = SmColor.Surface3)
        )
    }
}

@Composable
fun SmallChip(text: String, selected: Boolean = false, onClick: (() -> Unit)? = null) {
    Box(
        Modifier.clip(RoundedCornerShape(50)).background(if (selected) SmColor.Primary else SmColor.Surface3).border(1.dp, if (selected) SmColor.Primary else SmColor.Stroke, RoundedCornerShape(50))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 13.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = if (selected) Color(0xFF001018) else SmColor.Text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun InfoTag(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(50))
            .background(SmColor.Surface3)
            .border(1.dp, SmColor.Stroke, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text, color = SmColor.Text, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun KeyValueRow(label: String, value: String, color: Color = SmColor.Text) {
    Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = SmColor.TextMuted, fontSize = 14.sp)
        Spacer(Modifier.weight(1f))
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun NumericKeypad(onTap: (String) -> Unit, onBack: () -> Unit) {
    val rows = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf("-", "0", "⌫"))
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { key ->
                    Box(
                        Modifier.weight(1f).height(58.dp).clip(RoundedCornerShape(18.dp)).background(SmColor.Surface2).border(1.dp, SmColor.Stroke, RoundedCornerShape(18.dp))
                            .clickable { if (key == "⌫") onBack() else onTap(key) },
                        contentAlignment = Alignment.Center
                    ) { Text(key, color = SmColor.Text, fontSize = 22.sp, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
fun OptionGrid(options: List<String>, onSelect: (String) -> Unit) {
    val padded = if (options.size >= 4) options.take(4) else options + List(4 - options.size) { "" }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        padded.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { option ->
                    Box(
                        Modifier.weight(1f).height(72.dp).clip(RoundedCornerShape(22.dp)).background(SmColor.Surface2).border(1.dp, SmColor.Stroke, RoundedCornerShape(22.dp))
                            .clickable(enabled = option.isNotBlank()) { onSelect(option) },
                        contentAlignment = Alignment.Center
                    ) { Text(option, color = SmColor.Text, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold) }
                }
            }
        }
    }
}

@Composable
fun InputModeSwitcher(inputMode: InputMode, onToggle: () -> Unit) {
    Row(Modifier.clip(RoundedCornerShape(20.dp)).background(SmColor.Surface).padding(4.dp)) {
        listOf(InputMode.KEYPAD to "123", InputMode.OPTIONS to "▦").forEach { (mode, label) ->
            Box(Modifier.height(34.dp).width(54.dp).clip(RoundedCornerShape(15.dp)).background(if (inputMode == mode) SmColor.Primary else Color.Transparent).clickable { if (inputMode != mode) onToggle() }, contentAlignment = Alignment.Center) {
                Text(label, color = if (inputMode == mode) Color(0xFF001018) else SmColor.TextMuted, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AutoSubmitToggle(enabled: Boolean, onToggle: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(SmColor.Surface).clickable { onToggle() }.padding(horizontal = 10.dp, vertical = 8.dp)) {
        Text("Auto Submit", color = SmColor.TextMuted, fontSize = 12.sp)
        Spacer(Modifier.width(8.dp))
        Switch(checked = enabled, onCheckedChange = { onToggle() }, modifier = Modifier.height(24.dp), colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SmColor.Primary))
    }
}

@Composable
fun StatCounter(label: String, value: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(6.dp))
        Text("$label: $value", color = SmColor.Text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

fun LazyListScope.twoColumnCards(items: List<PracticeModule>, onClick: (PracticeModule) -> Unit) {
    items.chunked(2).forEach { row ->
        item {
            Row(Modifier.fillMaxWidth().padding(horizontal = ScreenPadding, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { m -> ModuleCard(m, Modifier.weight(1f), onClick = { onClick(m) }) }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}
