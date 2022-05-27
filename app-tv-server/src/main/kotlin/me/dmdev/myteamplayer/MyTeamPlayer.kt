package me.dmdev.myteamplayer

import android.util.Log
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyTeamPlayer(
    private val server: MyTeamPlayerServer,
    private val youTubePlayer: YouTubePlayer
) {

    private val mainScope = MainScope()
    private var state: State = State.IDLE

    fun start() {
        server.start()
        youTubePlayer.addListener(youTubePlayerListener)
        mainScope.launch {
            while (true) {
                if (state == State.IDLE) {
                    val video = server.nextTrack()
                    if (video != null) {
                        state = State.PLAYING
                        youTubePlayer.loadVideo(video, 0F)
                    }
                }
                delay(1000)
            }
        }
    }

    fun release() {
        server.stop()
        youTubePlayer.removeListener(youTubePlayerListener)
        mainScope.cancel()
    }

    fun stop() {
        youTubePlayer.pause()
        state = State.STOPED
    }

    fun pause() {
        youTubePlayer.pause()
        state = State.PAUSED
    }

    fun play() {
        state = if (state == State.PAUSED) {
            youTubePlayer.play()
            State.PLAYING
        } else {
            State.IDLE
        }
    }

    fun skipToNext() {
        youTubePlayer.pause()
        state = State.IDLE
    }

    private enum class State {
        IDLE, PLAYING, PAUSED, STOPED
    }

    private val youTubePlayerListener = object : AbstractYouTubePlayerListener() {
        override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
            state = State.IDLE
        }

        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerState
        ) {
            if (state == PlayerState.ENDED) {
                this@MyTeamPlayer.state = State.IDLE
            }
        }
    }
}