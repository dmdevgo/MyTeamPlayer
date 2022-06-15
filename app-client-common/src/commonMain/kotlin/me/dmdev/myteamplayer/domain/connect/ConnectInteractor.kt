package me.dmdev.myteamplayer.domain.connect

interface ConnectInteractor {
    suspend fun connect(address: String): ConnectionResult
}
