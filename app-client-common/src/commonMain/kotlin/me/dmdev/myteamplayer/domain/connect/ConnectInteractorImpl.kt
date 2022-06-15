package me.dmdev.myteamplayer.domain.connect

class ConnectInteractorImpl(
    private val serverApi: Connector
) : ConnectInteractor {

    override suspend fun connect(address: String): ConnectionResult {
        return try {
            if (serverApi.connect(address)) {
                ConnectionResult.CONNECTED
            } else {
                ConnectionResult.CONNECTION_ERROR
            }
        } catch (e: Throwable) {
            ConnectionResult.CONNECTION_ERROR
        }
    }
}