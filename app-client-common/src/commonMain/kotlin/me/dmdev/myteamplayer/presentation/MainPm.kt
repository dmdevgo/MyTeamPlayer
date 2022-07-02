package me.dmdev.myteamplayer.presentation

import kotlinx.serialization.Serializable
import me.dmdev.premo.PmDescription
import me.dmdev.premo.PmParams
import me.dmdev.premo.PresentationModel
import me.dmdev.premo.navigation.StackNavigation
import me.dmdev.premo.onMessage

class MainPm(
    params: PmParams
) : PresentationModel(params) {

    @Serializable
    object Description : PmDescription

    val navigation: StackNavigation = StackNavigation(
        initialDescription = ConnectPm.Description
    ) { navigator ->
        onMessage<ConnectPm.OnConnectedToServerMessage> {
            navigator.push(Child(PlayerPm.Description))
        }
    }
}

