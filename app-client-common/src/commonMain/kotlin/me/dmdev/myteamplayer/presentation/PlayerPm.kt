package me.dmdev.myteamplayer.presentation

import kotlinx.serialization.Serializable
import me.dmdev.myteamplayer.presentation.PlayerPm.State
import me.dmdev.premo.PmDescription
import me.dmdev.premo.PmParams

class PlayerPm(
    params: PmParams,
) : BasePm<State>(
    params,
    State()
) {

    @Serializable
    object Description : PmDescription

    class State



}
