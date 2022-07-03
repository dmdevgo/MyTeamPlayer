package me.dmdev.myteamplayer.presentation

import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.dmdev.myteamplayer.domain.player.PlayerClient
import me.dmdev.myteamplayer.model.PlayerState
import me.dmdev.premo.PmDescription
import me.dmdev.premo.PmLifecycle
import me.dmdev.premo.PmParams

class PlayerPm(
    params: PmParams,
    private val playerClient: PlayerClient
) : BasePm<PlayerState>(
    params,
    PlayerState(null)
) {

    @Serializable
    data class Description(
        val serverAddress: String
    ) : PmDescription

    init {
        startPlayer()
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
