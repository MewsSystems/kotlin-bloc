package com.mews.app.bloc.example

import com.mews.app.bloc.Emitter
import com.mews.app.bloc.android.BlocViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class MainViewModel : BlocViewModel<MainEvent, MainState>() {
    override val initialState: MainState = MainState()

    override suspend fun Emitter<MainState>.mapEventToState(event: MainEvent) = when (event) {
        MainEvent.Incremented -> emit(state.copy(value = state.value + 1))
        MainEvent.Decremented -> emit(state.copy(value = state.value - 1))
    }

    override fun Flow<MainEvent>.transformEvents(): Flow<MainEvent> = transform {
        emit(it)
        emit(it)
    }
}
