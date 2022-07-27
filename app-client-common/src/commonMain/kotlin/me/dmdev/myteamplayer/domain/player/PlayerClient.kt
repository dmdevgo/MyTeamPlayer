package me.dmdev.myteamplayer.domain.player

import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.dmdev.myteamplayer.domain.Api
import me.dmdev.myteamplayer.model.PlayerCommand
import me.dmdev.myteamplayer.model.PlayerInfo

class PlayerClient(
    private val api: Api
) {

    private val scope = MainScope()

    private val commands: MutableSharedFlow<PlayerCommand> = MutableSharedFlow()
    private val _state: MutableStateFlow<PlayerInfo> = MutableStateFlow(PlayerInfo(null,))
    val state: StateFlow<PlayerInfo> get() = _state.asStateFlow()

    fun start() {
        scope.launch {
            api.websockets {
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
