package me.dmdev.myteamplayer.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerInfo(
    val video: Video?,
    val queue: List<Video> = listOf(),
    val state: State = State.IDLE,
    val progressInSeconds: Int = 0,
    val volumeOn: Boolean = true,
    val volumeLevel: Int = 0,
    val keepCount: Int = 0,
    val skipCount: Int = 0,
) {
    enum class State {
        IDLE, PLAYING, PAUSED, STOPPED
    }

    val isPlaying: Boolean = state == State.PLAYING
    val progressInPercent: Float =
        if (video?.durationInSeconds != null && video.durationInSeconds > 0) {
            progressInSeconds.toFloat() / video.durationInSeconds.toFloat()
        } else {
            0F
        }

    val durationFormat: String = formatSeconds(video?.durationInSeconds)
    val progressFormat: String = formatSeconds(progressInSeconds)

    private fun formatSeconds(progress: Int?): String {
        return if (progress != null) {
            val hours = progress / 3600
            val minutes = progress % 3600 / 60
            val seconds = progress % 60

            val minutesFormat = if (minutes < 10) { "0$minutes" } else { minutes.toString() }
            val secondsFormat = if (seconds < 10) { "0$seconds" } else { seconds.toString() }

            if (hours > 0) {
                "$hours:$minutesFormat:$secondsFormat"
            } else {
                "$minutesFormat:$secondsFormat"
            }
        } else {
            "--:--"
        }
    }
}
