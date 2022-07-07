package me.dmdev.myteamplayer.domain.player

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.dmdev.myteamplayer.model.PlayerCommand
import me.dmdev.myteamplayer.model.PlayerInfo

class PlayerClient(
    private val serverHost: String
) {
    private val client = HttpClient(OkHttp) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }

    private val scope = MainScope()

    private val commands: MutableSharedFlow<PlayerCommand> = MutableSharedFlow()
    private val _state: MutableStateFlow<PlayerInfo> = MutableStateFlow(PlayerInfo(null,))
    val state: StateFlow<PlayerInfo> get() = _state.asStateFlow()

    fun start() {
        scope.launch {
            client.webSocket(
                method = HttpMethod.Get,
                host = serverHost,
                port = 8080,
                path = "/player"
            ) {
                val outputJob = launch { outputMessages() }
                val inputJob = launch { inputMessages() }
                inputJob.join()
                outputJob.cancelAndJoin()
            }
        }
    }

    fun sendCommand(command: PlayerCommand) {
        scope.launch {
            commands.emit(command)
        }
    }

    fun close() {
        scope.cancel()
        client.close()
    }

    private suspend fun DefaultClientWebSocketSession.inputMessages() {
        while (true) {
            _state.value = receiveDeserialized()
        }
    }

    private suspend fun DefaultClientWebSocketSession.outputMessages() {
        commands.collect {
            sendSerialized(it)
        }
    }
}

