package me.dmdev.myteamplayer

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.body
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h4
import kotlinx.html.head
import kotlinx.html.input
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.p
import kotlinx.html.title
import kotlinx.html.ul
import kotlinx.serialization.json.Json
import me.dmdev.myteamplayer.model.PlayerState
import me.dmdev.myteamplayer.model.Video
import org.json.JSONObject
import java.util.concurrent.LinkedBlockingQueue

class MyTeamPlayerServer {

    private var scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private val playQueue = LinkedBlockingQueue<Video>()
    private var playerState: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState(null))
    private var currentVideo: Video?
        get() = playerState.value.video
        set(value) {
            playerState.value = playerState.value.copy(
                video = value
            )
        }

    private var keepRequests = mutableSetOf<String>()
    private var skipRequests = mutableSetOf<String>()

    private val client = HttpClient(OkHttp)

    fun start() {
        scope.launch {
            server.start(wait = true)
        }
    }

    fun stop() {
        server.stop(1_000, 2_000)
        scope.cancel()
        client.close()
    }

    fun nextTrack(): String? {
        currentVideo = playQueue.poll()
        keepRequests.clear()
        skipRequests.clear()
        return currentVideo?.id
    }

    fun currentTrack(): String? {
        return currentVideo?.id
    }

    fun hasNextTrack(): Boolean {
        return playQueue.isNotEmpty()
    }

    fun getSkipRate(): Int {
        return skipRequests.size - keepRequests.size
    }

    fun updateDuration(duration: Float) {
        currentVideo = currentVideo?.copy(
            durationInSeconds = duration.toInt()
        )
    }

    fun updateIsPlaying(isPlaying: Boolean) {
        playerState.value = playerState.value.copy(
            isPlaying = isPlaying
        )
    }

    private val server = embeddedServer(Netty, port = 8080) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title {
                            +"MyTeamPlayer"
                        }
                        link()
                    }
                    body {
                        h1 {
                            +"My Team Player!"
                        }

                        form {
                            action = "video"
                            method = FormMethod.post
                            p {
                                input {
                                    type = InputType.text
                                    name = "video"
                                }
                            }
                            p {
                                input {
                                    type = InputType.submit
                                }
                            }
                        }

                        currentVideo?.let {
                            h4 {
                                +" Current video:"
                            }
                            p {
                                +it.title
                            }

                            p {
                                form {
                                    action = "keepVideo"
                                    method = FormMethod.post
                                    input {
                                        type = InputType.hidden
                                        name = "videoId"
                                        value = it.id
                                    }
                                    input {
                                        type = InputType.submit
                                        value = "Keep"
                                    }
                                    +" ${keepRequests.size}"
                                }
                            }
                            p {
                                form {
                                    action = "skipVideo"
                                    method = FormMethod.post
                                    input {
                                        type = InputType.hidden
                                        name = "videoId"
                                        value = it.id
                                    }
                                    input {
                                        type = InputType.submit
                                        value = "Skip"
                                    }
                                    + " ${skipRequests.size}"
                                }
                            }
                        }

                        h4 {
                            +"Play queue:"
                        }

                        ul {
                            playQueue.forEach { li { +it.title } }
                        }
                    }
                }
            }
            get("/hello") {
                call.respondText("Hello!")
            }
            webSocket("/player") {
                try {
                    val job = launch { this@webSocket.outputMessages() }
                    job.join()
                } catch (e: ClosedReceiveChannelException) {
                    println("onClose ${closeReason.await()}")
                } catch (e: Throwable) {
                    println("onError ${closeReason.await()}")
                    e.printStackTrace()
                }
            }
            post("/video") {
                val videoId = call.receiveParameters().getOrFail("video")
                    .trim()
                    .replace("https://youtu.be/", "")

                if (videoId.isNotBlank()) {
                    val video = loadVideo(videoId)
                    playQueue.offer(video)
                    call.respondRedirect("/")
                }
            }
            post("/keepVideo") {
                keepVideoRequest(
                    videoId = call.receiveParameters().getOrFail("videoId").trim(),
                    userId = call.request.origin.remoteHost
                )
                call.respondRedirect("/")
            }
            post("/skipVideo") {
                skipVideoRequest(
                    videoId = call.receiveParameters().getOrFail("videoId").trim(),
                    userId = call.request.origin.remoteHost
                )
                call.respondRedirect("/")
            }
        }
    }

    private suspend fun DefaultWebSocketServerSession.outputMessages() {
        playerState.collect {
            sendSerialized(it)
        }
    }

    private suspend fun loadVideo(videoId: String): Video {
        val response: HttpResponse =
            client.get("https://www.youtube.com/oembed?url=http://www.youtube.com/watch?v=${videoId}&format=json")
        val responseBody = response.bodyAsText()
        val json = JSONObject(responseBody)
        return Video(
            id = videoId,
            title = json.getString("title"),
            author = json.getString("author_name"),
            thumbnailUrl = json.getString("thumbnail_url"),
            durationInSeconds = null,
        )
    }

    private fun keepVideoRequest(videoId: String, userId: String) {
        if (currentVideo?.id == videoId) {
            keepRequests.add(userId)
            skipRequests.remove(userId)
        }
    }

    private fun skipVideoRequest(videoId: String, userId: String) {
        if (currentVideo?.id == videoId) {
            keepRequests.remove(userId)
            skipRequests.add(userId)
        }
    }
}

