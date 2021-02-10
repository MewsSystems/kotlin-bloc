package com.mews.app.bloc.example

import com.mews.app.bloc.android.BlocViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform

class MainViewModel : BlocViewModel<MainEvent, MainState>() {
    override val initialState: MainState = MainState()

    override suspend fun mapEventToState(event: MainEvent): Flow<MainState> = when (event) {
        MainEvent.Incremented -> flowOf(state.copy(value = state.value + 1))
        MainEvent.Decremented -> flowOf(state.copy(value = state.value - 1))
    }

    override fun transformEvents(events: Flow<MainEvent>): Flow<MainEvent> = events.transform {
        emit(it)
        emit(it)
    }
}
