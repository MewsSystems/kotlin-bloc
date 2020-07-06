package com.mews.app.bloc.example

import com.mews.app.bloc.Emitter
import com.mews.app.bloc.android.BlocViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext

class MainViewModel : BlocViewModel<MainEvent, MainState>() {
    override val initialState: MainState = MainState()

    override suspend fun Emitter<MainState>.mapEventToState(event: MainEvent) = when (event) {
        MainEvent.Incremented -> withContext(Dispatchers.IO) {
            // Do some heavy computations here
            Thread.sleep(1000)
            emit(state.copy(value = state.value + 1))
        }
        MainEvent.Decremented -> emit(state.copy(value = state.value - 1))
    }

    override fun Flow<MainEvent>.transformEvents(): Flow<MainEvent> = transform {
        println("asdasd")
        emit(it)
        emit(it)
    }
}
