package me.dmdev.myteamplayer

import android.annotation.SuppressLint
import android.app.Activity
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.format.Formatter
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


open class MyTeamPlayerActivity : Activity() {

    private val server by lazy { MyTeamPlayerServer(this) }
    private lateinit var youTubePlayerView: YouTubePlayerView
    private var player: MyTeamPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat

    private val playbackStateBuilder: PlaybackStateCompat.Builder = PlaybackStateCompat.Builder()
        .setActions(
            PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_team_player)
        youTubePlayerView = findViewById(R.id.youtube_player_view)
        youTubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                player = MyTeamPlayer(server, youTubePlayer)
                player?.start()
            }
        })
        mediaSession = MediaSessionCompat(this, "MyTeamPlayer").apply {
            setMediaButtonReceiver(null)
            setPlaybackState(playbackStateBuilder.build())
            setCallback(mediaSessionCallback)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        youTubePlayerView.release()
    }

    private fun getIpAddressInLocalNetwork(): String? {
        val wm = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        return Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
    }

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
            player?.play()
            mediaSession.setPlaybackState(
                playbackStateBuilder.setState(
                    PlaybackStateCompat.STATE_PLAYING,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1F
                ).build()
            )
        }

        override fun onPause() {
            super.onPause()
            player?.pause()
            mediaSession.setPlaybackState(
                playbackStateBuilder.setState(
                    PlaybackStateCompat.STATE_PAUSED,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1F
                ).build()
            )
        }

        override fun onStop() {
            super.onStop()
            player?.stop()
            mediaSession.setPlaybackState(
                playbackStateBuilder.setState(
                    PlaybackStateCompat.STATE_STOPPED,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1F
                ).build()
            )
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            player?.skipToNext()
            mediaSession.setPlaybackState(
                playbackStateBuilder.setState(
                    PlaybackStateCompat.STATE_PLAYING,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1F
                ).build()
            )
        }
    }
}
