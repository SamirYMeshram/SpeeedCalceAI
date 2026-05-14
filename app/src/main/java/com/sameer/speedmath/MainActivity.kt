package com.sameer.speedmath

import android.os.Bundle
import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sameer.speedmath.data.SpeedMathRepository
import com.sameer.speedmath.model.RootTab
import com.sameer.speedmath.ui.screens.*
import com.sameer.speedmath.ui.theme.SmColor
import com.sameer.speedmath.ui.theme.SpeedMathTheme
import com.sameer.speedmath.viewmodel.SpeedMathViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = AndroidColor.parseColor("#07111F")
        window.navigationBarColor = AndroidColor.BLACK
        setContent { SpeedMathTheme { SpeedMathApp() } }
    }
}

private sealed class AppRoute(val name: String) {
    data object Practise : AppRoute("practise")
    data object Revision : AppRoute("revision")
    data object Challenge : AppRoute("challenge")
    data object Dashboard : AppRoute("dashboard")
    data object Account : AppRoute("account")
    data object Settings : AppRoute("settings")
    data object ArithmeticMemory : AppRoute("arithmeticMemory")
    data object AttemptHistory : AppRoute("attemptHistory")
    data class AttemptReview(val id: String?) : AppRoute("attemptReview")
    data class RevisionTable(val topic: String) : AppRoute("revisionTable")
    data class Practice(val moduleId: String) : AppRoute("practice")
    data object Complexity : AppRoute("complexity")
    data object WorkoutIntro : AppRoute("workoutIntro")
    data object BattleIntro : AppRoute("battleIntro")
    data object BattleMode : AppRoute("battleMode")
    data object BattleCountdown : AppRoute("battleCountdown")
}

@Composable
fun SpeedMathApp() {
    val context = LocalContext.current.applicationContext
    val vm = remember { SpeedMathViewModel(SpeedMathRepository(context)) }
    val backStack = remember { mutableStateListOf<AppRoute>(AppRoute.Practise) }
    val current = backStack.last()

    fun popBackStack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    fun navigate(route: AppRoute) {
        val bottom = setOf("practise", "revision", "challenge", "dashboard", "account")
        if (route.name in bottom) {
            backStack.clear()
            backStack.add(route)
        } else {
            backStack.add(route)
        }
    }

    BackHandler(enabled = backStack.size > 1) {
        popBackStack()
    }

    Box(Modifier.fillMaxSize().background(SmColor.Background)) {
        val bottomRoutes = setOf("practise", "revision", "challenge", "dashboard", "account")
        Column(Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = current,
                transitionSpec = { fadeIn(tween(140)) togetherWith fadeOut(tween(110)) },
                modifier = Modifier.weight(1f),
                label = "routeFade"
            ) { route ->
                when (route) {
                    AppRoute.Practise -> PracticeHomeScreen(vm, onModuleClick = { m -> if (m.id == "complexity") navigate(AppRoute.Complexity) else navigate(AppRoute.Practice(m.id)) }, onArithmeticMemory = { navigate(AppRoute.ArithmeticMemory) })
                    AppRoute.Revision -> RevisionScreen(onOpenTable = { navigate(AppRoute.RevisionTable(it)) })
                    AppRoute.Challenge -> ChallengeScreen(vm, onStartError = { navigate(AppRoute.Practice("error_practice")) }, onWorkout = { navigate(AppRoute.WorkoutIntro) }, onBattle = { navigate(AppRoute.BattleIntro) }, onDaily = { navigate(AppRoute.Practice("daily")) })
                    AppRoute.Dashboard -> DashboardScreen(vm, onAttemptHistory = { navigate(AppRoute.AttemptHistory) })
                    AppRoute.Account -> AccountScreen(vm, onSettings = { navigate(AppRoute.Settings) })
                    AppRoute.Settings -> SettingsScreen(vm, onBack = ::popBackStack)
                    AppRoute.ArithmeticMemory -> ArithmeticMemoryScreen(vm, onBack = ::popBackStack)
                    AppRoute.AttemptHistory -> AttemptHistoryScreen(vm, onBack = ::popBackStack, onReview = { navigate(AppRoute.AttemptReview(it)) })
                    is AppRoute.AttemptReview -> AttemptReviewScreen(vm, sessionId = route.id, onBack = ::popBackStack)
                    is AppRoute.RevisionTable -> RevisionTableScreen(vm, topic = route.topic, onBack = ::popBackStack)
                    is AppRoute.Practice -> PracticeRoute(vm, moduleId = route.moduleId, onBack = ::popBackStack, onHistory = { navigate(AppRoute.AttemptHistory) })
                    AppRoute.Complexity -> ComplexityScreen(vm, onBack = ::popBackStack, onStart = { navigate(AppRoute.Practice("complexity")) })
                    AppRoute.WorkoutIntro -> WorkoutIntroScreen(onBack = ::popBackStack, onStart = { navigate(AppRoute.Practice("workout")) })
                    AppRoute.BattleIntro -> BattleIntroScreen(onBack = ::popBackStack, onStart = { navigate(AppRoute.BattleMode) })
                    AppRoute.BattleMode -> BattleModeScreen(onBack = ::popBackStack, onOnline = { navigate(AppRoute.BattleCountdown) }, onFriend = { navigate(AppRoute.BattleCountdown) })
                    AppRoute.BattleCountdown -> BattleCountdownScreen(onBack = ::popBackStack, onStartBattle = { navigate(AppRoute.Practice("battle")) })
                }
            }
            if (current.name in bottomRoutes) {
                BottomNavBar(currentRoute = current.name) { dest ->
                    navigate(
                        when (dest) {
                            "revision" -> AppRoute.Revision
                            "challenge" -> AppRoute.Challenge
                            "dashboard" -> AppRoute.Dashboard
                            "account" -> AppRoute.Account
                            else -> AppRoute.Practise
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavBar(currentRoute: String, onNavigate: (String) -> Unit) {
    val map = mapOf(
        RootTab.PRACTISE to "practise",
        RootTab.REVISION to "revision",
        RootTab.CHALLENGE to "challenge",
        RootTab.DASHBOARD to "dashboard",
        RootTab.ACCOUNT to "account"
    )
    Row(
        Modifier.fillMaxWidth()
            .background(SmColor.Background)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        map.forEach { (tab, dest) ->
            val active = currentRoute == dest
            Column(
                Modifier.weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (active) SmColor.Surface2 else Color.Transparent)
                    .clickable { onNavigate(dest) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(tab.icon, color = if (active) SmColor.Primary else SmColor.TextMuted, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(tab.label, color = if (active) SmColor.Primary else SmColor.TextMuted, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp)
            }
        }
    }
}
