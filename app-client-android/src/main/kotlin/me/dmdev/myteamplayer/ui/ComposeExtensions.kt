package me.dmdev.myteamplayer.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import me.dmdev.premo.ExperimentalPremoApi
import me.dmdev.premo.PresentationModel
import me.dmdev.premo.navigation.BackstackChange
import me.dmdev.premo.navigation.StackNavigation

@OptIn(ExperimentalPremoApi::class)
@Composable
fun NavigationBox(
    navigation: StackNavigation,
    modifier: Modifier = Modifier,
    content: @Composable (PresentationModel?) -> Unit
) {
    NavigationBox(
        backstackChange = navigation.backstackChanges.collectAsState(BackstackChange.Empty).value,
        modifier = modifier,
        content = content,
    )
}

@OptIn(ExperimentalPremoApi::class)
@Composable
fun NavigationBox(
    backstackChange: BackstackChange,
    modifier: Modifier = Modifier,
    content: @Composable (PresentationModel?) -> Unit
) {

    val stateHolder = rememberSaveableStateHolder()

    val pm = when (backstackChange) {
        is BackstackChange.Push -> {
            stateHolder.removeState(backstackChange.enterPm.tag)
            backstackChange.enterPm
        }
        is BackstackChange.Pop -> {
            stateHolder.removeState(backstackChange.exitPm.tag)
            backstackChange.enterPm
        }
        is BackstackChange.Set -> {
            backstackChange.pm
        }
        is BackstackChange.Empty -> null
    }

    Box(modifier) {
        stateHolder.SaveableStateProvider(pm?.tag ?: "") {
            content(pm)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalPremoApi::class)
@Composable
fun AnimatedNavigationBox(
    navigation: StackNavigation,
    modifier: Modifier = Modifier,
    enterTransition: ((initialPm: PresentationModel, targetPm: PresentationModel) -> EnterTransition) =
        { _, _ -> fadeIn() },
    exitTransition: ((initialPm: PresentationModel, targetPm: PresentationModel) -> ExitTransition) =
        { _, _ -> fadeOut() },
    popEnterTransition: ((initialPm: PresentationModel, targetPm: PresentationModel) -> EnterTransition) =
        { _, _ -> fadeIn() },
    popExitTransition: ((initialPm: PresentationModel, targetPm: PresentationModel) -> ExitTransition) =
        { _, _ -> fadeOut() },
    content: @Composable (PresentationModel?) -> Unit
) {
    val backStackChange = navigation.backstackChanges.collectAsState(BackstackChange.Empty).value

    AnimatedContent(
        targetState = backStackChange,
        transitionSpec = {
            when (backStackChange) {
                is BackstackChange.Push -> {
                    enterTransition(backStackChange.exitPm, backStackChange.enterPm) with
                            exitTransition(backStackChange.exitPm, backStackChange.enterPm)
                }
                is BackstackChange.Pop -> {
                    popEnterTransition(backStackChange.exitPm, backStackChange.enterPm) with
                            popExitTransition(backStackChange.exitPm, backStackChange.enterPm)
                }
                else -> {
                    fadeIn() with fadeOut()
                }
            }
        }
    ) { backstackChange: BackstackChange ->
        NavigationBox(
            backstackChange = backstackChange,
            modifier = modifier,
            content = content,
        )
    }
}