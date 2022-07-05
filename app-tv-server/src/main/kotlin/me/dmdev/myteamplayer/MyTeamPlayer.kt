package me.dmdev.myteamplayer

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.dmdev.myteamplayer.model.Video
import java.util.concurrent.LinkedBlockingQueue

class MyTeamPlayer(
    private val youTubePlayer: YouTubePlayer
) {

    companion object {
        const val PLAY_TIME_LIMIT: Long = 10L * 60000L // 10 minutes
        const val CRITICAL_SKIP_RATE = 2
        const val NORMAL_SKIP_RATE = 0
    }

    private val mainScope = MainScope()
    private val playQueue = LinkedBlockingQueue<Video>()
    var playerState: MutableStateFlow<me.dmdev.myteamplayer.model.PlayerState> = MutableStateFlow(
        me.dmdev.myteamplayer.model.PlayerState(null)
    )
    var currentVideo: Video?
        get() = playerState.value.video
        private set(value) {
            playerState.value = playerState.value.copy(
                video = value
            )
        }

    private var keepRequests = mutableSetOf<String>()
    val keepRequestsCount: Int get() = keepRequests.size

    private var skipRequests = mutableSetOf<String>()
    val skipRequestsCount: Int get() = skipRequests.size

    fun videosInQueue(): List<Video> {
        return playQueue.toList()
    }

    private var state: State = State.IDLE
        set(value) {
            updateIsPlaying(value == State.PLAYING)
            field = value
        }

    private var startTime: Long = 0L

    fun start() {
        youTubePlayer.addListener(youTubePlayerListener)
        mainScope.launch {
            while (true) {
                if (state == State.IDLE) {
                    val video: String?
                    val isNew: Boolean

                    when {
                        hasNextTrack() -> {
                            video = nextTrack()
                            isNew = true
                        }
                        getSkipRate() < NORMAL_SKIP_RATE -> {
                            video = currentVideo?.id
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
                if (state == State.PLAYING && startTime != 0L && hasNextTrack()) {
                    val isOutFromTimeLimit = System.currentTimeMillis() - startTime > PLAY_TIME_LIMIT
                    val skipRate = getSkipRate()
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

    fun offer(video: Video) {
        playQueue.offer(video)
    }

    fun keepVideoRequest(videoId: String, userId: String) {
        if (currentVideo?.id == videoId) {
            keepRequests.add(userId)
            skipRequests.remove(userId)
        }
    }

    fun skipVideoRequest(videoId: String, userId: String) {
        if (currentVideo?.id == videoId) {
            keepRequests.remove(userId)
            skipRequests.add(userId)
        }
    }

    private fun updateDuration(duration: Float) {
        currentVideo = currentVideo?.copy(
            durationInSeconds = duration.toInt()
        )
    }

    private fun updateIsPlaying(isPlaying: Boolean) {
        playerState.value = playerState.value.copy(
            isPlaying = isPlaying
        )
    }

    private fun nextTrack(): String? {
        currentVideo = playQueue.poll()
        keepRequests.clear()
        skipRequests.clear()
        return currentVideo?.id
    }

    private fun hasNextTrack(): Boolean {
        return playQueue.isNotEmpty()
    }

    private fun getSkipRate(): Int {
        return skipRequests.size - keepRequests.size
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
            updateDuration(duration)
        }
    }
}