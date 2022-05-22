package me.dmdev.myteamplayer

import android.annotation.SuppressLint
import android.app.Activity
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


open class MyTeamPlayerActivity : Activity() {

    private val server = MyTeamPlayerServer()
    private lateinit var youTubePlayerView: YouTubePlayerView
    private var player: MyTeamPlayer? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_team_player)
        youTubePlayerView = findViewById(R.id.youtube_player_view)
        youTubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback{
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                player = MyTeamPlayer(server, youTubePlayer)
                player?.start()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.stop()
        youTubePlayerView.release()
    }

    private fun getIpAddressInLocalNetwork(): String? {
        val wm = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        return Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
    }
}
