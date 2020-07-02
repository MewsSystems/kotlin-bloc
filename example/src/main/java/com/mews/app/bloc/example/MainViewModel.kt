package com.mews.app.bloc.example

import com.mews.app.bloc.android.BlocViewModel
import kotlinx.coroutines.flow.FlowCollector

class MainViewModel : BlocViewModel<Event, State>() {
    override val initialState: State =
        State()

    override suspend fun FlowCollector<State>.mapEventToState(event: Event) {
        when (event) {
            Event.Increment -> emit(state.copy(value = state.value + 1))
            Event.Decrement -> emit(state.copy(value = state.value - 1))
        }
    }
}
