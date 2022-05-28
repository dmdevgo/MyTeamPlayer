package me.dmdev.myteamplayer.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.dmdev.myteamplayer.R
import me.dmdev.myteamplayer.presentation.ConnectPm
import me.dmdev.myteamplayer.ui.WindowSizes
import me.dmdev.myteamplayer.ui.theme.MyTeamPlayerTheme
import me.dmdev.myteamplayer.ui.theme.custom_green_color

@Composable
fun ConnectScreen(
    serverAddress: String,
    windowSizes: WindowSizes,
    onServerAddressChange: ((String) -> Unit)
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp)
                .align(Alignment.Center)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.mipmap.ic_launcher),
                    contentDescription = "My team player logo"
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "My Team Player",
                    style = MaterialTheme.typography.titleLarge,
                    color = custom_green_color
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier.width(300.dp),
                value = serverAddress,
                label = { Text("Server address") },
                singleLine = true,
                onValueChange = onServerAddressChange,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Go,
                    autoCorrect = false
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = {}
            ) {
                Text("Connect")
            }
        }
    }
}

@Composable
fun ConnectScreen(
    pm: ConnectPm,
    windowSizes: WindowSizes
) {
    ConnectScreen(
        serverAddress = "0.0.0.0",
        windowSizes = windowSizes,
        onServerAddressChange = {}
    )
}

@Composable
@Preview(showBackground = true)
fun ConnectScreenPreview() {
    ConnectScreen(
        serverAddress = "0.0.0.0",
        windowSizes = WindowSizes.COMPACT,
        onServerAddressChange = {}
    )
}