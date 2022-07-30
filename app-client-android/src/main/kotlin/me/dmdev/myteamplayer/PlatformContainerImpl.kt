package me.dmdev.myteamplayer

import android.content.Context
import me.dmdev.myteamplayer.domain.Api
import me.dmdev.myteamplayer.domain.update.CheckUpdatesInteractor
import me.dmdev.myteamplayer.domain.update.CheckUpdatesInteractorImpl
import me.dmdev.myteamplayer.domain.update.DownloadUpdatesInteractor
import me.dmdev.myteamplayer.domain.update.DownloadUpdatesInteractorImpl
import me.dmdev.myteamplayer.model.AppVersion
import me.dmdev.myteamplayer.presentation.PlatformContainer

class PlatformContainerImpl(
    private val context: Context
): PlatformContainer {

    override fun createCheckUpdatesInteractor(api: Api): CheckUpdatesInteractor {
        return CheckUpdatesInteractorImpl(api, getAppVersion())
    }

    override fun createDownloadUpdatesInteractor(api: Api): DownloadUpdatesInteractor {
        return DownloadUpdatesInteractorImpl(api, context)
    }

    private fun getAppVersion(): AppVersion {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return AppVersion(packageInfo.versionCode, packageInfo.versionName)
    }
}