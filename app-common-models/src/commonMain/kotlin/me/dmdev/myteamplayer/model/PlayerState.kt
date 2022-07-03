package me.dmdev.myteamplayer.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerState(
    val video: Video?,
    val keepCount: Int = 0,
    val skipCount: Int = 0,
    val volumeLevel: Int = 0,
    val volumeOn: Boolean = true,
    val isPlaying: Boolean = true,
)
