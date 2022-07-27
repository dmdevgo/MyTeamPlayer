package me.dmdev.myteamplayer.domain.connect

class ConnectInteractorImpl(
    private val connector: Connector
) : ConnectInteractor {

    override suspend fun connect(address: String): ConnectionResult {
        return try {
            if (connector.connect(address)) {
                ConnectionResult.CONNECTED
            } else {
                ConnectionResult.CONNECTION_ERROR
            }
        } catch (e: Throwable) {
            ConnectionResult.CONNECTION_ERROR
        }
    }
}