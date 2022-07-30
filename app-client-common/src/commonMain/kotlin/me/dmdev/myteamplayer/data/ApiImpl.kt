package me.dmdev.myteamplayer.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import me.dmdev.myteamplayer.domain.Api
import me.dmdev.myteamplayer.domain.TargetPlatform
import me.dmdev.myteamplayer.model.Config

class ApiImpl(
    private val httpClient: HttpClient,
    serverAddress: String,
    port: Int = 8080
) : Api {

    private val url = "http://$serverAddress:$port"
    private val wsUrl = "ws://$serverAddress:$port"

    override suspend fun getConfig(): Config {
        return httpClient.get("$url/config").body()
    }

    override fun getDownloadUpdatesUrl(targetPlatform: TargetPlatform): String {
        return when (targetPlatform) {
            TargetPlatform.ANDROID -> "$url/download/android"
        }
    }

    override suspend fun websockets(block: suspend DefaultClientWebSocketSession.() -> Unit) {
        httpClient.webSocket("$wsUrl/player", block = block)
    }
}
