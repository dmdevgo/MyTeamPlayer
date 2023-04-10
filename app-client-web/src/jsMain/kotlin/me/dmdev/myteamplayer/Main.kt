package me.dmdev.myteamplayer

import me.dmdev.myteamplayer.ui.App
import react.create
import react.dom.client.createRoot
import web.dom.document

fun main() {
    val container = document.getElementById("root") ?: error("Couldn't find root container!")
    createRoot(container).render(App.create())
}
