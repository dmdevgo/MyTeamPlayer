package me.dmdev.myteamplayer.model

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val appVersions: AppVersions,
)
