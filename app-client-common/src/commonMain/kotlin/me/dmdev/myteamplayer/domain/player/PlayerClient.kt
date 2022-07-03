package me.dmdev.myteamplayer.domain.player

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.dmdev.myteamplayer.model.PlayerState

class PlayerClient(
    private val serverHost: String
) {
    private val client = HttpClient(OkHttp) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }

    private val job = MainScope()

    private val _state: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState(null))
    val state: StateFlow<PlayerState> get() = _state.asStateFlow()

    fun start() {
        job.launch {
            client.webSocket(
                method = HttpMethod.Get,
                host = serverHost,
                port = 8080,
                path = "/player"
            ) {
                val job = launch { inputMessages() }
                job.join()
            }
        }
    }

    fun close() {
        job.cancel()
        client.close()
    }

    private suspend fun DefaultClientWebSocketSession.inputMessages() {
        while (true) {
            _state.value = receiveDeserialized()
        }
    }
}

