package me.dmdev.myteamplayer.model

import kotlinx.serialization.Serializable

@Serializable
sealed class PlayerCommand {
    @Serializable
    object Play : PlayerCommand()

    @Serializable
    object Pause : PlayerCommand()
}

