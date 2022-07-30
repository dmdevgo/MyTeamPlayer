package me.dmdev.myteamplayer.presentation

import me.dmdev.myteamplayer.domain.Api
import me.dmdev.myteamplayer.domain.update.CheckUpdatesInteractor
import me.dmdev.myteamplayer.domain.update.DownloadUpdatesInteractor

interface PlatformContainer {
    fun createCheckUpdatesInteractor(api: Api): CheckUpdatesInteractor
    fun createDownloadUpdatesInteractor(api: Api): DownloadUpdatesInteractor
}