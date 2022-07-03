package me.dmdev.myteamplayer.presentation

import kotlinx.serialization.Serializable
import me.dmdev.myteamplayer.domain.player.PlayerClient
import me.dmdev.myteamplayer.presentation.PlayerPm.State
import me.dmdev.premo.PmDescription
import me.dmdev.premo.PmLifecycle
import me.dmdev.premo.PmParams

class PlayerPm(
    params: PmParams,
    private val playerClient: PlayerClient
) : BasePm<State>(
    params,
    State()
) {

    @Serializable
    data class Description(
        val serverAddress: String
    ) : PmDescription

    class State

    init {
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
        playerClient.start()
    }
}
