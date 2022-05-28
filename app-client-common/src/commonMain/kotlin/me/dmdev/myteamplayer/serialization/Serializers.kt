package me.dmdev.myteamplayer.serialization

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import me.dmdev.myteamplayer.presentation.ConnectPm
import me.dmdev.myteamplayer.presentation.MainPm
import me.dmdev.premo.PmDescription

object Serializers {

    val json =  Json {
        serializersModule = module
    }

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
    }
}