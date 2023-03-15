package me.dmdev.myteamplayer

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.format.Formatter
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.dmdev.myteamplayer.model.PlayerInfo


open class MyTeamPlayerActivity : Activity() {

    private var server: MyTeamPlayerServer? = null
    private var youTubePlayerView: YouTubePlayerView? = null
    private var infoView: View? = null
    private var qrCodeImage: ImageView? = null
    private var ipAddress: TextView? = null
    private var player: MyTeamPlayer? = null
    private var mediaSession: MediaSessionCompat? = null
    private val scope = MainScope()

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
        infoView = findViewById(R.id.info)
        qrCodeImage = findViewById(R.id.qr_code)
        ipAddress = findViewById(R.id.ip_address)
        youTubePlayerView = findViewById(R.id.youtube_player_view)
        youTubePlayerView?.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                player = MyTeamPlayer(youTubePlayer).also { player ->
                    player.start()
                    server = MyTeamPlayerServer(
                        this@MyTeamPlayerActivity,
                        player,
                        YoutubeRepository()
                    ).also { server ->
                        server.start()
                    }
                }
                subscribeToPlayerState()
            }
        })
        mediaSession = MediaSessionCompat(this, "MyTeamPlayer").apply {
            setMediaButtonReceiver(null)
            setPlaybackState(playbackStateBuilder.build())
            setCallback(mediaSessionCallback)
        }
        displayLinkAndQRCode()
    }

    private fun displayLinkAndQRCode() {
        val link = "http://${getIpAddressInLocalNetwork()}:8080/"
        ipAddress?.text = link
        val size = 512 //pixels
        val bits = QRCodeWriter().encode(link, BarcodeFormat.QR_CODE, size, size)
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) resources.getColor(R.color.ic_logo) else Color.TRANSPARENT)
                }
            }
        }
        if (bmp != null) {
            qrCodeImage?.setImageBitmap(bmp)
        }
    }

    private fun subscribeToPlayerState() {
        scope.launch {
            player?.infoFlow?.collect {
                if (it.state == PlayerInfo.State.IDLE) {
                    delay(3000)
                    if (player?.info?.state == PlayerInfo.State.IDLE) {
                        infoView?.visibility = View.VISIBLE
                        youTubePlayerView?.visibility = View.INVISIBLE
                    }
                } else {
                    infoView?.visibility = View.INVISIBLE
                    youTubePlayerView?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        server?.stop()
        youTubePlayerView?.release()
        scope.cancel()
    }

    private fun getIpAddressInLocalNetwork(): String? {
        val wm = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        return Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
    }

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
            player?.play()
            mediaSession?.setPlaybackState(
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
            mediaSession?.setPlaybackState(
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
            mediaSession?.setPlaybackState(
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
            mediaSession?.setPlaybackState(
                playbackStateBuilder.setState(
                    PlaybackStateCompat.STATE_PLAYING,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1F
                ).build()
            )
        }
    }
}
