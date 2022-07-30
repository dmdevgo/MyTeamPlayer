package me.dmdev.myteamplayer.presentation.player

import me.dmdev.myteamplayer.domain.Api
import me.dmdev.myteamplayer.presentation.PlatformContainer
import me.dmdev.premo.PmFactory
import me.dmdev.premo.PmParams
import me.dmdev.premo.PresentationModel

class PlayerContainer(
    private val api: Api,
    private val parentPmFactory: PmFactory,
    private val platformDepsFactory: PlatformContainer
) : PmFactory {

    override fun createPm(params: PmParams): PresentationModel {
        return when (val description = params.description) {
            is CheckUpdatesPm.Description -> createCheckUpdatesPm(params)
            else -> parentPmFactory.createPm(params)
        }
    }

    private fun createCheckUpdatesPm(params: PmParams): PresentationModel {
        return CheckUpdatesPm(
            params = params,
            checkUpdatesInteractor = platformDepsFactory.createCheckUpdatesInteractor(api),
            downloadUpdatesInteractor = platformDepsFactory.createDownloadUpdatesInteractor(api)
        )
    }
}