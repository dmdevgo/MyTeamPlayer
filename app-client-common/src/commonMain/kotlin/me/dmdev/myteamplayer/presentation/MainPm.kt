package me.dmdev.myteamplayer.presentation

import kotlinx.serialization.Serializable
import me.dmdev.premo.PmDescription
import me.dmdev.premo.PmParams
import me.dmdev.premo.PresentationModel
import me.dmdev.premo.navigation.StackNavigation
import me.dmdev.premo.navigation.StackNavigator

class MainPm(
    params: PmParams
) : PresentationModel(params) {

    @Serializable
    object Description : PmDescription

    val navigation: StackNavigation = StackNavigator(
        initialDescription = ConnectPm.Description
    )
}