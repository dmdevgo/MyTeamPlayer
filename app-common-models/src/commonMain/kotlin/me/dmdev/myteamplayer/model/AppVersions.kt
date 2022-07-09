package me.dmdev.myteamplayer.model

import kotlinx.serialization.Serializable

@Serializable
data class AppVersions(
    val androidClient: AppVersion,
    val androidTvServer: AppVersion,
)

@Serializable
data class AppVersion(
    val code: Int,
    val name: String
)