package me.dmdev.myteamplayer.domain

import io.ktor.client.plugins.websocket.*
import me.dmdev.myteamplayer.model.Config

interface Api {
    suspend fun getConfig(): Config
    suspend fun websockets(block: suspend DefaultClientWebSocketSession.() -> Unit)
}
