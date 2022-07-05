package me.dmdev.myteamplayer

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import me.dmdev.myteamplayer.model.Video
import org.json.JSONObject

class YoutubeRepository {

    private val client = HttpClient(OkHttp)

    suspend fun loadVideoInfo(videoId: String): Video {
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

}