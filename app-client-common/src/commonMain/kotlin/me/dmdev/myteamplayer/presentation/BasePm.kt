package me.dmdev.myteamplayer.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.dmdev.premo.PmParams
import me.dmdev.premo.PresentationModel

abstract class BasePm<S>(
    params: PmParams,
    initialState: S,
) : PresentationModel(params) {

    private val _state: MutableStateFlow<S> = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    protected fun changeState(copy: S.() -> S) {
        _state.value = copy(_state.value)
    }
}