package me.dmdev.myteamplayer

import android.content.Context
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
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
import me.dmdev.myteamplayer.model.AppVersion
import me.dmdev.myteamplayer.model.AppVersions
import me.dmdev.myteamplayer.model.Config
import me.dmdev.myteamplayer.model.PlayerCommand
import java.io.File


class MyTeamPlayerServer(
    private val context: Context,
    private val player: MyTeamPlayer,
    private val youtubeRepository: YoutubeRepository
) {

    private val webFolder = File("${context.filesDir.path}/$WEB_FOLDER")
    private var mainScope: CoroutineScope = MainScope()
    private var scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private val _commands: MutableSharedFlow<PlayerCommand> = MutableSharedFlow()

    fun start() {
        subscribeToCommands()
        scope.launch {
            copeWebFolderFromAssets()
            server.start(wait = true)
        }
    }

    fun stop() {
        server.stop(1_000, 2_000)
        scope.cancel()
        mainScope.cancel()
    }

    private val server = embeddedServer(Netty, port = 8080) {

        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
        install(PartialContent)
        install(AutoHeadResponse)

        routing {
            static("/") {
                staticRootFolder = webFolder
                file("app-client-web.js")
                file(APK_FILE_NAME)
            }

            get("/download/android") {
                val file = File("${webFolder.path}/$APK_FILE_NAME.apk")
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName,
                        "my-team-player-debug.apk"
                    ).toString()
                )
                call.respondFile(file)
            }
            get("/client") {
                call.respondText(
                    File("${webFolder.path}/index.html").readText(),
                    ContentType.Text.Html
                )
            }
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

                        a {
                            href = "/download/android"
                            text("Android apk")
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

                        player.info.video?.let {
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
                                    +" ${player.info.keepCount}"
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
                                    + " ${player.info.skipCount}"
                                }
                            }
                        }

                        h4 {
                            +"Play queue:"
                        }

                        ul {
                            player.info.queue.forEach { li { +it.title } }
                        }
                    }
                }
            }
            get("/hello") {
                call.respondText("Hello!")
            }
            get("/config") {
                call.respond(config)
            }
            webSocket("/player") {
                try {
                    this.call.request.origin.remoteHost
                    val outputJob = launch { outputMessages() }
                    val inputJob = launch { inputMessages() }
                    inputJob.join()
                    outputJob.cancelAndJoin()
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
                    val video = youtubeRepository.loadVideoInfo(videoId)
                    player.offer(video)
                    call.respondRedirect("/")
                }
            }
            post("/keepVideo") {
                player.keepVideo(
                    userId = call.request.origin.remoteHost
                )
                call.respondRedirect("/")
            }
            post("/skipVideo") {
                player.skipVideo(
                    userId = call.request.origin.remoteHost
                )
                call.respondRedirect("/")
            }
        }
    }

    private suspend fun DefaultWebSocketServerSession.inputMessages() {
        while (true) {
            var command = receiveDeserialized<PlayerCommand>()
            command = when (command) {
                is PlayerCommand.Keep -> PlayerCommand.Keep(call.request.origin.remoteHost)
                is PlayerCommand.Skip -> PlayerCommand.Skip(call.request.origin.remoteHost)
                else -> command
            }
            _commands.emit(command)
        }
    }

    private suspend fun DefaultWebSocketServerSession.outputMessages() {
        player.infoFlow.collect {
            sendSerialized(it)
        }
    }

    private fun subscribeToCommands() {
        mainScope.launch {
            _commands.collect { command ->
                when (command) {
                    is PlayerCommand.Play -> player.play()
                    is PlayerCommand.Pause -> player.pause()
                    is PlayerCommand.Mute -> player.mute()
                    is PlayerCommand.UnMute -> player.unMute()
                    is PlayerCommand.SetVolume -> player.setVolume(command.volume)
                    is PlayerCommand.Keep -> player.keepVideo(command.userId)
                    is PlayerCommand.Skip -> player.skipVideo(command.userId)
                    else -> {}
                }
            }
        }
    }

    private fun copeWebFolderFromAssets() {
        val files = context.assets.list(WEB_FOLDER)
        files?.forEach { fileName ->
            context.assets.open("$WEB_FOLDER/$fileName").use { input ->
                webFolder.mkdir()
                val file = File("${webFolder.path}/$fileName")
                file.createNewFile()
                file.outputStream().use { output -> input.copyTo(output) }
            }
        }
    }
    
    private val config: Config by lazy {
        
        val clientPackageInfo = context.packageManager
            .getPackageArchiveInfo("${webFolder.path}/$APK_FILE_NAME", 0)

        val androidTvServerPackageInfo = context.packageManager
            .getPackageInfo(context.packageName, 0)
        
        val androidClientVersion = if (clientPackageInfo != null) {
            AppVersion(clientPackageInfo.versionCode, clientPackageInfo.versionName)
        } else {
            AppVersion(0, "")
        }

        val androidTvServerVersion = if (androidTvServerPackageInfo != null) {
            AppVersion(androidTvServerPackageInfo.versionCode, androidTvServerPackageInfo.versionName)
        } else {
            AppVersion(0, "")
        }
        
        Config(
            appVersions = AppVersions(
                androidClient = androidClientVersion,
                androidTvServer = androidTvServerVersion
            )
        )
    }

    companion object {
        private const val WEB_FOLDER = "web"
        private const val APK_FILE_NAME = "app-client-android-debug.apk"
    }
}
