package me.dmdev.myteamplayer.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.window.layout.WindowMetricsCalculator

enum class WindowSizeClass { Compact, Medium, Expanded }

data class WindowSizes(
    val widthSizeClass: WindowSizeClass,
    val heightSizeClass: WindowSizeClass,
) {
    companion object {
        val COMPACT = WindowSizes(WindowSizeClass.Compact, WindowSizeClass.Compact)
    }
}

@Composable
fun Activity.rememberWindowSizes(): WindowSizes {
    val configuration = LocalConfiguration.current
    val windowMetrics = remember(configuration) {
        WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(this)
    }

    val windowDpSize = with(LocalDensity.current) {
        windowMetrics.bounds.toComposeRect().size.toDpSize()
    }

    val widthWindowSizeClass = when {
        windowDpSize.width < 600.dp -> WindowSizeClass.Compact
        windowDpSize.width < 840.dp -> WindowSizeClass.Medium
        else -> WindowSizeClass.Expanded
    }

    val heightWindowSizeClass = when {
        windowDpSize.height < 480.dp -> WindowSizeClass.Compact
        windowDpSize.height < 900.dp -> WindowSizeClass.Medium
        else -> WindowSizeClass.Expanded
    }

    return WindowSizes(widthWindowSizeClass, heightWindowSizeClass)
}