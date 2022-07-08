package me.dmdev.myteamplayer

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.dmdev.myteamplayer.model.PlayerInfo
import me.dmdev.myteamplayer.model.PlayerInfo.State
import me.dmdev.myteamplayer.model.Video

class MyTeamPlayer(
    private val youTubePlayer: YouTubePlayer
) {

    companion object {
        const val PLAY_TIME_LIMIT_IN_SECONDS: Long = 600L // 10 minutes
        const val CRITICAL_SKIP_RATE = 2
        const val NORMAL_SKIP_RATE = 0
    }

    private val mainScope = MainScope()

    private var keepRequests = mutableSetOf<String>()
    private var skipRequests = mutableSetOf<String>()

    private var _infoFlow: MutableStateFlow<PlayerInfo> =
        MutableStateFlow(PlayerInfo(null,))
    val infoFlow: StateFlow<PlayerInfo> = _infoFlow.asStateFlow()
    val info: PlayerInfo get() = infoFlow.value
    private fun updatePlayerInfo(copy: PlayerInfo.() -> PlayerInfo) {
        _infoFlow.value = copy(_infoFlow.value)
    }

    fun start() {
        youTubePlayer.addListener(youTubePlayerListener)
        mainScope.launch {
            while (true) {
                playNextTrackIfIdle()
                delay(1000)
                checkIfNeedToSkip()
            }
        }
    }

    fun play() {
        if (info.state == State.PAUSED) {
            youTubePlayer.play()
            updatePlayerInfo {
                copy(state = State.PLAYING)
            }
        } else {
            updatePlayerInfo {
                copy(state = State.IDLE)
            }
        }
    }

    fun pause() {
        youTubePlayer.pause()
        updatePlayerInfo {
            copy(state = State.PAUSED)
        }
    }
    fun stop() {
        youTubePlayer.pause()
        updatePlayerInfo {
            copy(
                progressInSeconds = 0,
                keepCount = 0,
                skipCount = 0,
                state = State.STOPPED
            )
        }
    }

    fun skipToNext() {
        youTubePlayer.pause()
        updatePlayerInfo {
            copy(state = State.IDLE)
        }
    }

    fun offer(video: Video) {
        updatePlayerInfo {
            copy(queue = queue.plus(video))
        }
    }

    fun keepVideo(userId: String) {
        keepRequests.add(userId)
        skipRequests.remove(userId)
        updatePlayerInfo {
            copy(
                keepCount = keepRequests.size,
                skipCount = skipRequests.size,
            )
        }
    }

    fun skipVideo(userId: String) {
        keepRequests.remove(userId)
        skipRequests.add(userId)
        updatePlayerInfo {
            copy(
                keepCount = keepRequests.size,
                skipCount = skipRequests.size,
            )
        }
    }

    fun mute() {
        youTubePlayer.mute()
        updatePlayerInfo {
            copy(volumeOn = false)
        }
    }

    fun unMute() {
        youTubePlayer.unMute()
        updatePlayerInfo {
            copy(volumeOn = true)
        }
    }

    fun setVolume(percent: Int) {
        if (percent in 0..100) {
            youTubePlayer.setVolume(percent)
            updatePlayerInfo {
                copy(volume = percent)
            }
        }
    }

    fun release() {
        youTubePlayer.removeListener(youTubePlayerListener)
        mainScope.cancel()
    }

    private fun checkIfNeedToSkip() {
        if (info.state == State.PLAYING && info.queue.isNotEmpty()) {
            val isOutFromTimeLimit = info.progressInSeconds > PLAY_TIME_LIMIT_IN_SECONDS
            val skipRate = skipRequests.size - keepRequests.size
            if (
                skipRate >= CRITICAL_SKIP_RATE ||
                isOutFromTimeLimit && skipRate > NORMAL_SKIP_RATE
            ) {
                skipToNext()
            }
        }
    }

    private fun playNextTrackIfIdle() {
        if (info.state == State.IDLE) {
            if (info.queue.isNotEmpty()) {
                val video = info.queue.first()
                updatePlayerInfo {
                    copy(
                        video = video,
                        queue = queue.drop(1),
                        keepCount = 0,
                        skipCount = 0,
                        progressInSeconds = 0,
                        state = State.PLAYING
                    )
                }
                keepRequests.clear()
                skipRequests.clear()
                youTubePlayer.loadVideo(video.id, 0F)
            }
        }
    }

    private val youTubePlayerListener = object : AbstractYouTubePlayerListener() {

        override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
            updatePlayerInfo {
                copy(
                    progressInSeconds = 0,
                    state = State.IDLE
                )
            }
        }

        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerState
        ) {
            if (state == PlayerState.ENDED) {
                updatePlayerInfo {
                    copy(state = State.IDLE)
                }
            }
        }

        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
            updatePlayerInfo {
                copy(progressInSeconds = second.toInt())
            }
        }

        override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
            updatePlayerInfo {
                copy(video = video?.copy(durationInSeconds = duration.toInt()))
            }
        }
    }
}