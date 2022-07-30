package me.dmdev.myteamplayer.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import me.dmdev.myteamplayer.presentation.player.CheckUpdatesPm
import me.dmdev.myteamplayer.ui.WindowSizes
import me.dmdev.myteamplayer.ui.theme.MyTeamPlayerTheme


@Composable
fun UpdatesAvailableScreen(
    showUpdatesAlert: Boolean,
    downloadClick: () -> Unit = {},
    closeClick: () -> Unit = {},
    windowSizes: WindowSizes,
    content: @Composable () -> Unit
) {
    Box {
        content()
        if (showUpdatesAlert) {
            AlertDialog(
                title = { Text("New version") },
                text = { Text("The application is outdated, download the latest version?") },
                onDismissRequest = closeClick,
                dismissButton = {
                    TextButton(onClick = closeClick) {
                        Text("Cancel")
                    }
                },
                confirmButton = {
                    TextButton(onClick = downloadClick) {
                        Text("Download")
                    }
                },
            )
        }
    }
}

@Composable
fun UpdatesAvailableBind(
    pm: CheckUpdatesPm,
    windowSizes: WindowSizes,
    content: @Composable () -> Unit
) {
    UpdatesAvailableScreen(
        showUpdatesAlert = pm.showAvailableUpdates.collectAsState().value,
        downloadClick = pm::downloadClick,
        closeClick = pm::closeClick,
        windowSizes = windowSizes,
        content = content
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun UpdatesAvailablePreview() {
    MyTeamPlayerTheme {
        UpdatesAvailableScreen(
            showUpdatesAlert = true,
            windowSizes = WindowSizes.COMPACT
        ) {
            PlayerScreenPreview()
        }
    }
}