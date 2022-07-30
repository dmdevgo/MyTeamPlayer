package me.dmdev.myteamplayer.presentation.player

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.dmdev.myteamplayer.domain.update.CheckUpdatesInteractor
import me.dmdev.myteamplayer.domain.update.DownloadUpdatesInteractor
import me.dmdev.premo.PmDescription
import me.dmdev.premo.PmParams
import me.dmdev.premo.PresentationModel

class CheckUpdatesPm(
    params: PmParams,
    private val checkUpdatesInteractor: CheckUpdatesInteractor,
    private val downloadUpdatesInteractor: DownloadUpdatesInteractor
) : PresentationModel(params) {

    @Serializable
    object Description : PmDescription

    private val _showAvailableUpdates: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showAvailableUpdates: StateFlow<Boolean> = _showAvailableUpdates.asStateFlow()

    init {
        checkUpdates()
    }

    private fun checkUpdates() {
        scope.launch {
            try {
                delay(1000) // show alert with delay
                _showAvailableUpdates.value = checkUpdatesInteractor.checkAvailableUpdates()
            } catch (e: Throwable) {
                // do nothing
            }
        }
    }

    fun downloadClick() {
        downloadUpdatesInteractor.download()
        _showAvailableUpdates.value = false
    }

    fun closeClick() {
        _showAvailableUpdates.value = false
    }
}
