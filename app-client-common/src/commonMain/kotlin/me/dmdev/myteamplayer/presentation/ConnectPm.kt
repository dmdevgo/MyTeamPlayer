package me.dmdev.myteamplayer.presentation

import kotlinx.serialization.Serializable
import me.dmdev.premo.PmDescription
import me.dmdev.premo.PmParams

class ConnectPm(
    params: PmParams
) : BasePm<ConnectPm.State>(
    params,
    State(
        serverAddress = "",
        isConnecting = false,
        errorMessage = "No connection",
    )
) {

    @Serializable
    object Description : PmDescription

    data class State(
        val serverAddress: String,
        val isConnecting: Boolean,
        val errorMessage: String,
    )

    fun onServerAddressChange(serverAddress: String) {
        changeState {
            copy(
                serverAddress = serverAddress,
                errorMessage = ""
            )
        }
    }

    fun onConnectClick() {
        // todo
    }
}
