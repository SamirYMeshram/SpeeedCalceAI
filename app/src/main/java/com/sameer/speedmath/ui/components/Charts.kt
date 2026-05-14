package com.sameer.speedmath.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameer.speedmath.model.ChartPoint
import com.sameer.speedmath.ui.theme.SmColor

@Composable
fun LineChartCard(title: String, points: List<ChartPoint>, color: Color, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth(), radius = 24) {
        Text(title, color = SmColor.Text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(12.dp))
        Canvas(Modifier.fillMaxWidth().height(160.dp)) {
            val max = (points.maxOfOrNull { it.value } ?: 1f).coerceAtLeast(1f)
            val min = (points.minOfOrNull { it.value } ?: 0f).coerceAtMost(max - 1f)
            val h = size.height
            val w = size.width
            repeat(4) { i ->
                val y = h * (i + 1) / 5
                drawLine(SmColor.Stroke.copy(alpha = 0.5f), Offset(0f, y), Offset(w, y), 1.dp.toPx())
            }
            if (points.size >= 2) {
                val step = w / (points.size - 1).coerceAtLeast(1)
                val offsets = points.mapIndexed { i, p ->
                    val y = h - ((p.value - min) / (max - min).coerceAtLeast(0.001f)) * (h - 16.dp.toPx()) - 8.dp.toPx()
                    Offset(i * step, y)
                }
                val path = Path().apply {
                    moveTo(offsets.first().x, offsets.first().y)
                    offsets.drop(1).forEach { lineTo(it.x, it.y) }
                }
                drawPath(path, color, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))
                offsets.forEach { drawCircle(color, 4.dp.toPx(), it) }
            }
        }
    }
}

@Composable
fun DonutChartCard(title: String, points: List<ChartPoint>) {
    GlassCard(Modifier.fillMaxWidth(), radius = 24) {
        Text(title, color = SmColor.Text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth()) {
            Canvas(Modifier.size(150.dp)) {
                val total = points.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)
                var start = -90f
                val colors = listOf(SmColor.Primary, SmColor.Purple, SmColor.Green, SmColor.Orange, SmColor.Red, SmColor.Pink, SmColor.Yellow, SmColor.PrimarySoft)
                points.forEachIndexed { i, p ->
                    val sweep = p.value / total * 360f
                    drawArc(colors[i % colors.size], start, sweep, false, style = Stroke(26.dp.toPx(), cap = StrokeCap.Butt), topLeft = Offset(18.dp.toPx(), 18.dp.toPx()), size = androidx.compose.ui.geometry.Size(size.width - 36.dp.toPx(), size.height - 36.dp.toPx()))
                    start += sweep
                }
            }
            Spacer(Modifier.width(18.dp))
            Column(Modifier.weight(1f)) {
                val colors = listOf(SmColor.Primary, SmColor.Purple, SmColor.Green, SmColor.Orange, SmColor.Red, SmColor.Pink, SmColor.Yellow, SmColor.PrimarySoft)
                points.take(9).forEachIndexed { i, p ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Canvas(Modifier.size(10.dp)) { drawCircle(colors[i % colors.size]) }
                        Spacer(Modifier.width(8.dp))
                        Text(p.label, color = SmColor.TextMuted, fontSize = 12.sp, modifier = Modifier.weight(1f), maxLines = 1)
                        Text(p.value.toInt().toString(), color = SmColor.Text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
