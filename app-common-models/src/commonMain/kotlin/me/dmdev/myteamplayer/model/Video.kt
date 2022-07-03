package me.dmdev.myteamplayer.model

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val id: String,
    val title: String,
    val author: String,
    val thumbnailUrl: String,
    val durationInSeconds: Int?,
)
