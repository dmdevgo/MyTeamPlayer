package me.dmdev.myteamplayer.domain.update

import me.dmdev.myteamplayer.domain.Api
import me.dmdev.myteamplayer.model.AppVersion

class CheckUpdatesInteractorImpl(
    private val api: Api,
    private val appVersion: AppVersion,
) : CheckUpdatesInteractor {

    override suspend fun checkAvailableUpdates(): Boolean {
        return api.getConfig().appVersions.androidClient.code > appVersion.code
    }
}