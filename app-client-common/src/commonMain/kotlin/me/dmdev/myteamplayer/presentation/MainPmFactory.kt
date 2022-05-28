package me.dmdev.myteamplayer.presentation

import me.dmdev.premo.PmFactory
import me.dmdev.premo.PmParams
import me.dmdev.premo.PresentationModel

class MainPmFactory : PmFactory {

    override fun createPm(params: PmParams): PresentationModel {
        return when (val description = params.description) {
            is MainPm.Description -> MainPm(params)
            is ConnectPm.Description -> ConnectPm(params)
            else -> throw IllegalArgumentException(
                "Not handled instance creation for pm description $description"
            )
        }
    }
}