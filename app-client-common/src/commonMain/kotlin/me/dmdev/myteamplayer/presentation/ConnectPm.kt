package me.dmdev.myteamplayer.presentation

import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.dmdev.myteamplayer.domain.connect.ConnectInteractor
import me.dmdev.myteamplayer.domain.connect.ConnectionResult
import me.dmdev.premo.PmDescription
import me.dmdev.premo.PmMessage
import me.dmdev.premo.PmParams

class ConnectPm(
    params: PmParams,
    private val connectInteractor: ConnectInteractor
) : BasePm<ConnectPm.State>(
    params,
    State(
        serverAddress = "",
        isConnecting = false,
        errorMessage = "",
    )
) {

    @Serializable
    object Description : PmDescription

    object OnConnectedToServerMessage : PmMessage

    data class State(
        val serverAddress: String,
        val isConnecting: Boolean,
        val errorMessage: String,
    ) {
        val connectEnabled: Boolean
            get() = serverAddress.isNotBlank() && isConnecting.not()
    }

    fun onServerAddressChange(serverAddress: String) {
        changeState {
            copy(
                serverAddress = serverAddress,
                errorMessage = ""
            )
        }
    }

    fun onConnectClick() {
        if (state.serverAddress.isNotBlank() && state.isConnecting.not()) {
            scope.launch {
                changeState {
                    copy(
                        isConnecting = true,
                        errorMessage = ""
                    )
                }

                val result = connectInteractor.connect(state.serverAddress)
                val error = when (result) {
                    ConnectionResult.CONNECTION_ERROR -> "Connection error"
                    else -> ""
                }
                changeState {
                    copy(
                        isConnecting = false,
                        errorMessage = error
                    )
                }
                if (result == ConnectionResult.CONNECTED) {
                    messageHandler.send(OnConnectedToServerMessage)
                }
            }
        }
    }
}
