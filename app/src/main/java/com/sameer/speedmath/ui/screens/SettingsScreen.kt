package com.sameer.speedmath.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameer.speedmath.ui.components.*
import com.sameer.speedmath.ui.theme.SmColor
import com.sameer.speedmath.viewmodel.SpeedMathViewModel

@Composable
fun SettingsScreen(vm: SpeedMathViewModel, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(horizontal = ScreenPadding, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) { RoundIcon("‹", onBack); Spacer(Modifier.width(12.dp)); Text("Settings", color = SmColor.Text, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp) }
        Column(Modifier.padding(horizontal = ScreenPadding), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsRow("Notification Settings", "Reminders and alerts") {}
            SettingsRow("Theme Mode", vm.settings.themeMode) {}
            SettingsRow("Language", vm.settings.language) {}
            ToggleRow("Auto Submit", "At answer length", vm.settings.autoSubmit, vm::toggleAutoSubmit)
            ToggleRow("Sound Effects", "Question and result sounds", vm.settings.soundEffects, vm::toggleSounds)
        }
    }
}

@Composable private fun SettingsRow(title: String, subtitle: String, onClick: () -> Unit) { GlassCard(onClick = onClick, radius = 20) { Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(title, color = SmColor.Text, fontWeight = FontWeight.Bold); Text(subtitle, color = SmColor.TextMuted, fontSize = 12.sp) }; Text("›", color = SmColor.TextMuted, fontSize = 24.sp) } } }
@Composable private fun ToggleRow(title: String, subtitle: String, checked: Boolean, onToggle: () -> Unit) { GlassCard(radius = 20) { Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(title, color = SmColor.Text, fontWeight = FontWeight.Bold); Text(subtitle, color = SmColor.TextMuted, fontSize = 12.sp) }; Switch(checked = checked, onCheckedChange = { onToggle() }, colors = SwitchDefaults.colors(checkedTrackColor = SmColor.Primary, checkedThumbColor = SmColor.Text)) } } }
