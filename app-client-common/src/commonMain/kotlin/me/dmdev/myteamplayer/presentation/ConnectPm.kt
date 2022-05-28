package me.dmdev.myteamplayer.presentation

import kotlinx.serialization.Serializable
import me.dmdev.premo.PmDescription
import me.dmdev.premo.PmParams
import me.dmdev.premo.PresentationModel

class ConnectPm(
    params: PmParams
) : PresentationModel(params) {

    @Serializable
    object Description : PmDescription
}

