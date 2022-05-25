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
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    private var job: Job? = null

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
        return playQueue.poll()?.id
    }

    private val server = embeddedServer(Netty, port = 8080) {
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

                        h4 {
                            +"Play queue:"
                        }

                        ul {
                            playQueue.forEach { li { +it.title } }
                        }
                    }
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
        }
    }

    private suspend fun loadVideoTitle(videoId: String): String {
        val response: HttpResponse =
            client.get("https://www.youtube.com/oembed?url=http://www.youtube.com/watch?v=${videoId}&format=json")
        val responseBody = response.bodyAsText()
        val json = JSONObject(responseBody)
        return json.getString("title")
    }
}