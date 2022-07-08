package me.dmdev.myteamplayer.presentation

import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.dmdev.myteamplayer.domain.player.PlayerClient
import me.dmdev.myteamplayer.model.PlayerCommand
import me.dmdev.myteamplayer.model.PlayerInfo
import me.dmdev.premo.PmDescription
import me.dmdev.premo.PmLifecycle
import me.dmdev.premo.PmParams

class PlayerPm(
    params: PmParams,
    private val playerClient: PlayerClient
) : BasePm<PlayerInfo>(
    params,
    PlayerInfo(null,)
) {

    @Serializable
    data class Description(
        val serverAddress: String
    ) : PmDescription

    init {
        startPlayer()
    }

    fun togglePlayPause() {
        playerClient.sendCommand(
            if (state.isPlaying) {
                PlayerCommand.Pause
            } else {
                PlayerCommand.Play
            }
        )
    }

    fun toggleMute() {
        playerClient.sendCommand(
            if (state.volumeOn) {
                PlayerCommand.Mute
            } else {
                PlayerCommand.UnMute
            }
        )
    }

    fun volumeUp() {
        playerClient.sendCommand(
            PlayerCommand.SetVolume(state.volume + 5)
        )
    }

    fun volumeDown() {
        playerClient.sendCommand(
            PlayerCommand.SetVolume(state.volume - 5)
        )
    }

    fun keep() {
        playerClient.sendCommand(
            PlayerCommand.Keep()
        )
    }

    fun skip() {
        playerClient.sendCommand(
            PlayerCommand.Skip()
        )
    }

    private fun startPlayer() {
        lifecycle.addObserver(object : PmLifecycle.Observer {
            override fun onLifecycleChange(lifecycle: PmLifecycle, event: PmLifecycle.Event) {
                when (event) {
                    PmLifecycle.Event.ON_DESTROY -> {
                        playerClient.close()
                    }
                    else -> {}
                }
            }
        })
        scope.launch {
            playerClient.state.collect {
                changeState { it }
            }
        }
        playerClient.start()
    }
}
