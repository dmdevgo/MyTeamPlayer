package me.dmdev.myteamplayer.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import me.dmdev.myteamplayer.ui.theme.MyTeamPlayerTheme
import me.dmdev.myteamplayer.ui.theme.custom_green_color

@Preview(showBackground = true)
@Composable
fun ConnectScreen() {

    var text = remember { mutableStateOf("") }

    MyTeamPlayerTheme(useDarkTheme = true) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher),
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
                value = text.value,
                label = { Text("Server address") },
                singleLine = true,
                onValueChange = { text.value = it },
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