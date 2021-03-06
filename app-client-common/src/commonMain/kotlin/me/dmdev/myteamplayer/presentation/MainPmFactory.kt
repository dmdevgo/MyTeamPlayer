package me.dmdev.myteamplayer.presentation

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import me.dmdev.myteamplayer.data.ConnectorImpl
import me.dmdev.myteamplayer.domain.connect.ConnectInteractorImpl
import me.dmdev.myteamplayer.domain.player.PlayerClient
import me.dmdev.premo.PmFactory
import me.dmdev.premo.PmParams
import me.dmdev.premo.PresentationModel

class MainPmFactory : PmFactory {

    override fun createPm(params: PmParams): PresentationModel {
        return when (val description = params.description) {
            is MainPm.Description -> MainPm(params)
            is ConnectPm.Description -> createConnectPm(params)
            is PlayerPm.Description -> createPlayerPm(params, description)
            else -> throw IllegalArgumentException(
                "Not handled instance creation for pm description $description"
            )
        }
    }

    private fun createPlayerPm(params: PmParams, description: PlayerPm.Description): PlayerPm {
        return PlayerPm(
            params,
            PlayerClient(description.serverAddress)
        )
    }

    private fun createConnectPm(params: PmParams): ConnectPm {
        return ConnectPm(
            params = params,
            connectInteractor = ConnectInteractorImpl(ConnectorImpl(HttpClient(OkHttp)))
        )
    }
}
