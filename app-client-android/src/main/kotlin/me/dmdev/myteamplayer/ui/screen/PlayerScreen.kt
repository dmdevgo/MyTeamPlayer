package me.dmdev.myteamplayer.ui.screen

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicVideo
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.VolumeDown
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.dmdev.myteamplayer.ui.WindowSizes
import me.dmdev.myteamplayer.ui.theme.MyTeamPlayerTheme
import me.dmdev.myteamplayer.ui.theme.custom_green_color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    windowSizes: WindowSizes,
) {

    Column {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            text = "My Team Player",
            style = MaterialTheme.typography.titleLarge,
            color = custom_green_color
        )
        OutlinedCard(
            Modifier.padding(12.dp)
        ) {
            Column(
                Modifier.padding(12.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Icon(
                        Icons.Default.MusicVideo,
                        "My team player logo",
                        Modifier.size(36.dp),
                        custom_green_color
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Relaxing Jazz Music - Background Chill Out Music - Music For Relax, Study, Work")
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = 0.9F
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Rounded.Pause, "pause")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Rounded.VolumeDown, "volume down")
                    }
                    Text(
                        text = "59",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(onClick = {}) {
                        Icon(Icons.Rounded.VolumeUp, "volume up")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Rounded.VolumeOff, "volume off")
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    Text(
                        text = "3:00 / 5:48",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    OutlinedButton(
                        modifier = Modifier.weight(0.3F),
                        onClick = {}
                    ) {
                        Text("Keep : 2")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        modifier = Modifier.weight(0.3F),
                        onClick = {}
                    ) {
                        Text("Skip : 1")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        modifier = Modifier.weight(0.4F),
                        onClick = {}
                    ) {
                        Text("Reactions : 3")
                    }
                }
            }
        }
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
        PlayerScreen(windowSizes = WindowSizes.COMPACT)
    }
}