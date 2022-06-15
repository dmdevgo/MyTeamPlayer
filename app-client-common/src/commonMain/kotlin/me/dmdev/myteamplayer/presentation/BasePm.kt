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

    private val _stateFlow: MutableStateFlow<S> = MutableStateFlow(initialState)
    val stateFlow: StateFlow<S> = _stateFlow.asStateFlow()
    val state: S get() = stateFlow.value

    protected fun changeState(copy: S.() -> S) {
        _stateFlow.value = copy(_stateFlow.value)
    }
}