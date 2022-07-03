package me.dmdev.myteamplayer.ui.screen

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.dmdev.myteamplayer.presentation.ConnectPm
import me.dmdev.myteamplayer.presentation.MainPm
import me.dmdev.myteamplayer.presentation.PlayerPm
import me.dmdev.myteamplayer.ui.AnimatedNavigationBox
import me.dmdev.myteamplayer.ui.WindowSizes

@Composable
fun MainScreen(mainPm: MainPm, windowSizes: WindowSizes) {
    AnimatedNavigationBox(
        navigation = mainPm.navigation,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { _, _ -> slideInHorizontally { height -> height } },
        exitTransition = { _, _ -> slideOutHorizontally { height -> -height } },
        popEnterTransition = { _, _ -> slideInHorizontally { height -> -height } },
        popExitTransition = { _, _ -> slideOutHorizontally { height -> height } },
    ) { pm ->
        when (pm) {
            is ConnectPm ->  ConnectScreenBind(pm, windowSizes)
            is PlayerPm -> PlayerScreenBind(pm, windowSizes)
            else -> {}
        }
    }
}