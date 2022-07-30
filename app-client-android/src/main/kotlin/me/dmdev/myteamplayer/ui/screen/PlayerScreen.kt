package me.dmdev.myteamplayer.ui.screen

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.VolumeDown
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.dmdev.myteamplayer.model.PlayerInfo
import me.dmdev.myteamplayer.model.Video
import me.dmdev.myteamplayer.presentation.player.PlayerPm
import me.dmdev.myteamplayer.ui.WindowSizes
import me.dmdev.myteamplayer.ui.theme.MyTeamPlayerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    info: PlayerInfo,
    windowSizes: WindowSizes,
    onPlayPauseClick: () -> Unit = {},
    onVolumeOnOffClick: () -> Unit = {},
    onVolumeUpClick: () -> Unit = {},
    onVolumeDownClick: () -> Unit = {},
    onKeepClick: () -> Unit = {},
    onSkipClick: () -> Unit = {}
) {

    Column {
        Spacer(modifier = Modifier.height(16.dp))
        MyTeamPlayerLogo(modifier = Modifier.fillMaxWidth())
        OutlinedCard(
            Modifier.padding(12.dp)
        ) {
            Column(
                Modifier.padding(12.dp)
            ) {
                Text(text = info.video?.title ?: "")
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = info.progressInPercent
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPlayPauseClick) {
                        if (info.isPlaying) {
                            Icon(Icons.Rounded.Pause, "pause")
                        } else {
                            Icon(Icons.Rounded.PlayArrow, "play")
                        }
                    }
                    IconButton(onClick = onVolumeDownClick) {
                        Icon(Icons.Rounded.VolumeDown, "volume down")
                    }
                    Text(
                        text = info.volume.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(onClick = onVolumeUpClick) {
                        Icon(Icons.Rounded.VolumeUp, "volume up")
                    }
                    IconButton(onClick = onVolumeOnOffClick) {
                        Icon(
                            Icons.Rounded.VolumeOff,
                            "volume off",
                            tint = if (info.volumeOn) {
                                LocalContentColor.current
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    Text(
                        text = "${info.progressFormat} / ${info.durationFormat}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    OutlinedButton(
                        modifier = Modifier.weight(0.3F),
                        onClick = onKeepClick
                    ) {
                        Text("Keep : ${info.keepCount}")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        modifier = Modifier.weight(0.3F),
                        onClick = onSkipClick
                    ) {
                        Text("Skip : ${info.skipCount}")
                    }
                }
            }
        }
    }

}

@Composable
fun PlayerScreenBind(
    pm: PlayerPm,
    windowSizes: WindowSizes
) {
    val state = pm.stateFlow.collectAsState().value

    UpdatesAvailableBind(
        pm = pm.checkUpdatesPm,
        windowSizes = windowSizes
    ) {
        PlayerScreen(
            info = state,
            windowSizes = windowSizes,
            onPlayPauseClick = pm::togglePlayPause,
            onVolumeOnOffClick = pm::toggleMute,
            onVolumeUpClick = pm::volumeUp,
            onVolumeDownClick = pm::volumeDown,
            onKeepClick = pm::keep,
            onSkipClick = pm::skip,
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = UI_MODE_NIGHT_YES
)
fun PlayerScreenPreview() {
    MyTeamPlayerTheme {
        PlayerScreen(
            info = PlayerInfo(
                video = Video(
                    id = "",
                    title = "Relaxing Jazz Music - Background Chill Out Music - Music For Relax, Study, Work",
                    author = "",
                    thumbnailUrl = "",
                    durationInSeconds = 100
                ),
                progressInSeconds = 70,
                volume = 80
            ),
            windowSizes = WindowSizes.COMPACT,
        )
    }
}