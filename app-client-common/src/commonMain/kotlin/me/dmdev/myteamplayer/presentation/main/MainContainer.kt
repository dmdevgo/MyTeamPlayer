package me.dmdev.myteamplayer.presentation.main

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import me.dmdev.myteamplayer.data.ApiImpl
import me.dmdev.myteamplayer.data.ConnectorImpl
import me.dmdev.myteamplayer.domain.Api
import me.dmdev.myteamplayer.domain.connect.ConnectInteractorImpl
import me.dmdev.myteamplayer.domain.player.PlayerClient
import me.dmdev.myteamplayer.presentation.PlatformContainer
import me.dmdev.myteamplayer.presentation.player.PlayerContainer
import me.dmdev.myteamplayer.presentation.player.PlayerPm
import me.dmdev.premo.PmDescription
import me.dmdev.premo.PmFactory
import me.dmdev.premo.PmParams
import me.dmdev.premo.PmStateSaverFactory
import me.dmdev.premo.PresentationModel

class MainContainer(
    private val platformDepsFactory: PlatformContainer
) : PmFactory {

    private val httpClient: HttpClient by lazy {
        HttpClient(OkHttp) {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
            install(ContentNegotiation) { json() }
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

    private fun createConnectPm(params: PmParams): ConnectPm {
        return ConnectPm(params, ConnectInteractorImpl(ConnectorImpl(httpClient)))
    }

    private fun createPlayerPm(
        params: PmParams,
        description: PlayerPm.Description
    ): PresentationModel {
        val api: Api = ApiImpl(httpClient, description.serverAddress)
        val playerClient = PlayerClient(ApiImpl(httpClient, description.serverAddress))
        val playerContainer = PlayerContainer(
            api = api,
            parentPmFactory = this,
            platformDepsFactory = platformDepsFactory
        )
        return PlayerPm(
            params = params.copy(factory = playerContainer),
            playerClient = playerClient
        )
    }

}

fun PmParams.copy(
    tag: String = this.tag,
    description: PmDescription = this.description,
    parent: PresentationModel? = this.parent,
    factory: PmFactory = this.factory,
    stateSaverFactory: PmStateSaverFactory = this.stateSaverFactory
): PmParams {
    return PmParams(
        tag = tag,
        description = description,
        parent = parent,
        factory = factory,
        stateSaverFactory = stateSaverFactory
    )
}
