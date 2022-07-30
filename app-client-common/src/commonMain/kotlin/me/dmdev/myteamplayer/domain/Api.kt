package me.dmdev.myteamplayer.domain

import io.ktor.client.plugins.websocket.*
import me.dmdev.myteamplayer.model.Config

interface Api {
    fun getDownloadUpdatesUrl(targetPlatform: TargetPlatform): String
    suspend fun getConfig(): Config
    suspend fun websockets(block: suspend DefaultClientWebSocketSession.() -> Unit)
}
