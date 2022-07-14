package me.dmdev.myteamplayer

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import me.dmdev.myteamplayer.model.Video
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject

class YoutubeRepository {

    private val client = HttpClient(OkHttp)

    suspend fun loadVideoInfo(videoPath: String): Video? {
        val videoId = extractVideoId(videoPath) ?: return null
        val response: HttpResponse =
            client.get("https://www.youtube.com/oembed?url=http://www.youtube.com/watch?v=${videoId}&format=json")
        val responseBody = response.bodyAsText()
        val json = JSONObject(responseBody)
        return Video(
            id = videoId,
            title = json.getString("title"),
            author = json.getString("author_name"),
            thumbnailUrl = json.getString("thumbnail_url"),
            durationInSeconds = null,
        )
    }

    private fun extractVideoId(videoPath: String): String? {
        val videoUrl = videoPath.toHttpUrlOrNull() ?: return videoPath

        val host = videoUrl.host.trim()
            .replace(Regex("^[wW]{3}\\."), "") // Remove www from beginning if exists.

        return when {
            host == "youtube.com" && videoUrl.encodedPath == "/watch" -> {
                videoUrl.queryParameter("v")
            }
            host == "youtu.be" && videoUrl.pathSize == 1 -> {
                videoUrl.pathSegments[0]
            }
            else -> null
        }
    }

}