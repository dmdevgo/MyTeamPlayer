package me.dmdev.myteamplayer.domain.connect

interface Connector {
    suspend fun connect(address: String): Boolean
}
