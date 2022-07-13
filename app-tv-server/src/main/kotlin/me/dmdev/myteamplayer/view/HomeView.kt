package me.dmdev.myteamplayer.view

import kotlinx.html.*
import me.dmdev.myteamplayer.MyTeamPlayer
import me.dmdev.myteamplayer.model.Video

fun HTML.homeView(player: MyTeamPlayer) {
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
                asHyperLink(it)
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
                    +" ${player.info.skipCount}"
                }
            }
        }

        h4 {
            +"Play queue:"
        }

        ul {
            player.info.queue.forEach { li { asHyperLink(it) } }
        }
    }
}

private fun FlowOrInteractiveOrPhrasingContent.asHyperLink(video: Video) {
    a {
        href = "https://youtu.be/${video.id}"
        text(video.title)
    }
}