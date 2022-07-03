package me.dmdev.myteamplayer

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
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
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ClosedReceiveChannelException
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
import org.json.JSONObject
import java.util.concurrent.LinkedBlockingQueue

class MyTeamPlayerServer {

    private val playQueue = LinkedBlockingQueue<MyVideo>()
    private var currentVideo: MyVideo? = null
    private var job: Job? = null

    private var keepRequests = mutableSetOf<String>()
    private var skipRequests = mutableSetOf<String>()

    private val client = HttpClient(OkHttp)

    fun start() {
        job = CoroutineScope(Dispatchers.IO).launch {
            server.start(wait = true)
        }
    }

    fun stop() {
        server.stop(1_000, 2_000)
        job?.cancel()
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

    private val server = embeddedServer(Netty, port = 8080) {
        install(WebSockets)
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
                    for (frame in incoming){
                        val msg = (frame as Frame.Text).readText()
                        println("Message: $msg")
                        when (msg) {
                            "Hello!" -> send(Frame.Text("Hi!"))
                            "Bye" -> close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                        }
                    }
                } catch (e: ClosedReceiveChannelException) {
                    println("onClose ${closeReason.await()}")
                } catch (e: Throwable) {
                    println("onError ${closeReason.await()}")
                    e.printStackTrace()
                }
            }
            post("/video") {
                val video = call.receiveParameters().getOrFail("video")
                    .trim()
                    .replace("https://youtu.be/", "")

                if (video.isNotBlank()) {
                    val title = loadVideoTitle(video)
                    playQueue.offer(MyVideo(title, video))
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

    private suspend fun loadVideoTitle(videoId: String): String {
        val response: HttpResponse =
            client.get("https://www.youtube.com/oembed?url=http://www.youtube.com/watch?v=${videoId}&format=json")
        val responseBody = response.bodyAsText()
        val json = JSONObject(responseBody)
        return json.getString("title")
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