package me.dmdev.myteamplayer.model

import kotlinx.serialization.Serializable

@Serializable
sealed class PlayerCommand {
    @Serializable
    object Play : PlayerCommand()

    @Serializable
    object Pause : PlayerCommand()

    @Serializable
    object Mute : PlayerCommand()

    @Serializable
    object UnMute : PlayerCommand()

    @Serializable
    data class SetVolume(val volume: Int) : PlayerCommand()

    @Serializable
    data class Keep(val userId: String = "") : PlayerCommand()

    @Serializable
    data class Skip(val userId: String = "") : PlayerCommand()
}

