package me.dmdev.myteamplayer.domain.update


interface CheckUpdatesInteractor {
    suspend fun checkAvailableUpdates(): Boolean
}
