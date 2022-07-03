package me.dmdev.myteamplayer

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyTeamPlayer(
    private val server: MyTeamPlayerServer,
    private val youTubePlayer: YouTubePlayer
) {

    companion object {
        const val PLAY_TIME_LIMIT: Long = 10L * 60000L // 10 minutes
        const val CRITICAL_SKIP_RATE = 2
        const val NORMAL_SKIP_RATE = 0
    }

    private val mainScope = MainScope()
    private var state: State = State.IDLE
        set(value) {
            server.updateIsPlaying(value == State.PLAYING)
            field = value
        }

    private var startTime: Long = 0L

    fun start() {
        server.start()
        youTubePlayer.addListener(youTubePlayerListener)
        mainScope.launch {
            while (true) {
                if (state == State.IDLE) {
                    val video: String?
                    val isNew: Boolean

                    when {
                        server.hasNextTrack() -> {
                            video = server.nextTrack()
                            isNew = true
                        }
                        server.getSkipRate() < NORMAL_SKIP_RATE -> {
                            video = server.currentTrack()
                            isNew = false
                        }
                        else -> {
                            video = null
                            isNew = false
                        }
                    }

                    if (video != null) {
                        state = State.PLAYING
                        youTubePlayer.loadVideo(video, 0F)
                        if (isNew) {
                            startTime = System.currentTimeMillis()
                        }
                    }
                }
                delay(1000)
                if (state == State.PLAYING && startTime != 0L && server.hasNextTrack()) {
                    val isOutFromTimeLimit = System.currentTimeMillis() - startTime > PLAY_TIME_LIMIT
                    val skipRate = server.getSkipRate()
                    if (
                        skipRate >= CRITICAL_SKIP_RATE ||
                        isOutFromTimeLimit && skipRate > NORMAL_SKIP_RATE
                    ) {
                        skipToNext()
                    }
                }
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
        state = State.STOPPED
    }

    fun pause() {
        youTubePlayer.pause()
        state = State.PAUSED
    }

    fun play() {
        state = if (state == State.PAUSED) {
            youTubePlayer.play()
            startTime = System.currentTimeMillis()
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
        IDLE, PLAYING, PAUSED, STOPPED
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

        override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
            server.updateDuration(duration)
        }
    }
}