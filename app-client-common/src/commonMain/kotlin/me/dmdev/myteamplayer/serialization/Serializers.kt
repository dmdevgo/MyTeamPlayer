package me.dmdev.myteamplayer.serialization

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import me.dmdev.myteamplayer.presentation.ConnectPm
import me.dmdev.myteamplayer.presentation.PlayerPm
import me.dmdev.myteamplayer.presentation.main.MainPm
import me.dmdev.premo.PmDescription

object Serializers {

    private val module = SerializersModule {
        polymorphic(
            PmDescription::class,
            MainPm.Description::class,
            MainPm.Description.serializer()
        )
        polymorphic(
            PmDescription::class,
            ConnectPm.Description::class,
            ConnectPm.Description.serializer()
        )
        polymorphic(
            PmDescription::class,
            PlayerPm.Description::class,
            PlayerPm.Description.serializer()
        )
    }

    val json = Json {
        serializersModule = module
    }
}