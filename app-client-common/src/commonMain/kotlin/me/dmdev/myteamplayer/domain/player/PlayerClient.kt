package me.dmdev.myteamplayer.domain.player

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PlayerClient(
    private val serverHost: String
) {
    private val client = HttpClient(OkHttp) {
        install(WebSockets)
    }

    private val job = MainScope()

    fun start() {
        job.launch {
            client.webSocket(method = HttpMethod.Get, host = serverHost, port = 8080, path = "/player") {
                send(Frame.Text("Hello!"))
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val msg = String(frame.data)
                            println("MSG: $msg")
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun close() {
        job.cancel()
        client.close()
    }
}