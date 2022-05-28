package me.dmdev.myteamplayer.serialization

import kotlin.reflect.KType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import me.dmdev.myteamplayer.serialization.Serializers.json
import me.dmdev.premo.PmStateSaver

class JsonPmStateSaver(
    private val map: MutableMap<String, String> = mutableMapOf()
) : PmStateSaver {

    override fun <T> saveState(key: String, kType: KType, value: T?) {
        @Suppress("UNCHECKED_CAST")
        if (value != null) {
            map[key] = json.encodeToString(serializer(kType) as KSerializer<T>, value)
        }
    }

    override fun <T> restoreState(key: String, kType: KType): T? {
        @Suppress("UNCHECKED_CAST")
        return map[key]?.let {
            json.decodeFromString(serializer(kType) as KSerializer<T>, it)
        }
    }
}