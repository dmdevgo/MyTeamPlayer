package me.dmdev.myteamplayer.data

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.dmdev.myteamplayer.domain.connect.Connector

class ConnectorImpl(
    private val client: HttpClient
) : Connector {

    override suspend fun connect(address: String): Boolean {
        return client.get("http://$address:8080/hello").status == HttpStatusCode.OK
    }
}