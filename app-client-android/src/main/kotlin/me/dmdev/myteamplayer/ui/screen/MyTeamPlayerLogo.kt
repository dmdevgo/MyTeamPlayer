package me.dmdev.myteamplayer.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicVideo
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.dmdev.myteamplayer.ui.theme.custom_green_color

@Composable
@Preview
fun MyTeamPlayerLogo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.MusicVideo,
            "My team player logo",
            Modifier.size(36.dp),
            custom_green_color
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "My Team Player",
            style = MaterialTheme.typography.titleLarge,
            color = custom_green_color
        )
    }
}