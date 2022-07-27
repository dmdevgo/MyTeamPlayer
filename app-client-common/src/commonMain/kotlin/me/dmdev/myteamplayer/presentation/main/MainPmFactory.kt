package me.dmdev.myteamplayer.presentation.main

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import kotlinx.serialization.json.Json
import me.dmdev.myteamplayer.data.ApiImpl
import me.dmdev.myteamplayer.data.ConnectorImpl
import me.dmdev.myteamplayer.domain.connect.ConnectInteractorImpl
import me.dmdev.myteamplayer.domain.player.PlayerClient
import me.dmdev.myteamplayer.presentation.ConnectPm
import me.dmdev.myteamplayer.presentation.PlayerPm
import me.dmdev.premo.PmFactory
import me.dmdev.premo.PmParams
import me.dmdev.premo.PresentationModel

class MainPmFactory : PmFactory {

    private val httpClient: HttpClient by lazy {
        HttpClient(OkHttp) {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }
    }

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

    private fun createPlayerPm(
        params: PmParams,
        description: PlayerPm.Description
    ): PresentationModel {
        return PlayerPm(params, PlayerClient(ApiImpl(httpClient, description.serverAddress)))
    }

    private fun createConnectPm(params: PmParams): ConnectPm {
        return ConnectPm(params, ConnectInteractorImpl(ConnectorImpl(httpClient)))
    }
}
