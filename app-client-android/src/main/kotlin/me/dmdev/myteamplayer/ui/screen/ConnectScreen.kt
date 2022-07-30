package me.dmdev.myteamplayer.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.dmdev.myteamplayer.presentation.main.ConnectPm
import me.dmdev.myteamplayer.ui.WindowSizes

@Composable
fun ConnectScreen(
    serverAddress: String,
    connectEnabled: Boolean,
    isConnecting: Boolean,
    errorMessage: String,
    windowSizes: WindowSizes,
    onServerAddressChange: ((String) -> Unit),
    onConnectClick: (() -> Unit),
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp)
                .align(Alignment.Center)
        ) {
            MyTeamPlayerLogo(
                modifier = Modifier.padding(start = 16.dp)
            )
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
                ),
                keyboardActions = KeyboardActions(
                    onGo = { onConnectClick() }
                )
            )
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(start = 16.dp, top = 4.dp)
                    .alpha(if (errorMessage.isNotBlank()) 1f else 0f)
            )
            Button(
                modifier = Modifier.align(Alignment.End),
                enabled = connectEnabled,
                onClick = onConnectClick
            ) {
                Text("Connect")
            }
        }
    }
}

@Composable
fun ConnectScreenBind(
    pm: ConnectPm,
    windowSizes: WindowSizes
) {
    val state = pm.stateFlow.collectAsState().value
    ConnectScreen(
        serverAddress = state.serverAddress,
        connectEnabled = state.connectEnabled,
        isConnecting = state.isConnecting,
        errorMessage = state.errorMessage,
        windowSizes = windowSizes,
        onServerAddressChange = pm::onServerAddressChange,
        onConnectClick = pm::onConnectClick,
    )
}

@Composable
@Preview(showBackground = true)
fun ConnectScreenPreview() {
    ConnectScreen(
        serverAddress = "0.0.0.0",
        connectEnabled = true,
        isConnecting = false,
        errorMessage = "No connection",
        windowSizes = WindowSizes.COMPACT,
        onServerAddressChange = {},
        onConnectClick = {}
    )
}