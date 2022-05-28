package me.dmdev.myteamplayer.ui.screen

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.dmdev.myteamplayer.presentation.ConnectPm
import me.dmdev.myteamplayer.presentation.MainPm
import me.dmdev.myteamplayer.ui.AnimatedNavigationBox
import me.dmdev.myteamplayer.ui.WindowSizes

@Composable
fun MainScreen(pm: MainPm, windowSizes: WindowSizes) {
    AnimatedNavigationBox(
        navigation = pm.navigation,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { _, _ -> slideInHorizontally { height -> height } },
        exitTransition = { _, _ -> slideOutHorizontally { height -> -height } },
        popEnterTransition = { _, _ -> slideInHorizontally { height -> -height } },
        popExitTransition = { _, _ -> slideOutHorizontally { height -> height } },
    ) { pm ->
        when (pm) {
            is ConnectPm -> ConnectScreen(pm, windowSizes)
            else -> {}
        }
    }
}