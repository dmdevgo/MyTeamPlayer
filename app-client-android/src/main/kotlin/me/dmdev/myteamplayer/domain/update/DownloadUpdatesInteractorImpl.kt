package me.dmdev.myteamplayer.domain.update

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import me.dmdev.myteamplayer.domain.Api
import me.dmdev.myteamplayer.domain.TargetPlatform


class DownloadUpdatesInteractorImpl(
    private val api: Api,
    private val context: Context
) : DownloadUpdatesInteractor {

    override fun download() {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri: Uri = Uri.parse(api.getDownloadUpdatesUrl(TargetPlatform.ANDROID))
        val request = DownloadManager.Request(uri)
        request.setTitle("My Team Player")
        request.setDescription("Downloading")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "my-team-player"
        )
        downloadManager.enqueue(request)
    }
}
