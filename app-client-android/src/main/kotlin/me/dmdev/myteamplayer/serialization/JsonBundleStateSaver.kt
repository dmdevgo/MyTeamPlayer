package me.dmdev.myteamplayer.serialization

import android.os.Bundle
import kotlinx.serialization.serializer
import me.dmdev.myteamplayer.serialization.Serializers.json
import me.dmdev.premo.BundleStateSaver
import me.dmdev.premo.PmStateSaver

class JsonBundleStateSaver : BundleStateSaver {

    private var pmStates = mutableMapOf<String, MutableMap<String, String>>()

    override fun save(outState: Bundle) {
        outState.putString(PM_STATE_KEY, json.encodeToString(serializer(), pmStates))
    }

    override fun restore(savedState: Bundle?) {
        savedState?.getString(PM_STATE_KEY)?.let { jsonString ->
            pmStates = json.decodeFromString(serializer(), jsonString)
        }
    }

    override fun createPmStateSaver(key: String): PmStateSaver {
        val map = pmStates[key] ?: mutableMapOf<String, String>().also { pmStates[key] = it }
        return JsonPmStateSaver(map)
    }

    companion object {
        private const val PM_STATE_KEY = "pm_state"
    }
}