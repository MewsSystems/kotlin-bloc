package com.mews.app.bloc.example

import com.mews.app.bloc.BaseBloc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector

sealed class Event {
    object Increment : Event()
    object Decrement : Event()
}

data class State(val value: Int = 0)

class MainBloc(scope: CoroutineScope) : BaseBloc<Event, State>(scope) {
    override val initialState: State =
        State()

    override suspend fun FlowCollector<State>.mapEventToState(event: Event) {
        when (event) {
            Event.Increment -> emit(state.copy(value = state.value + 1))
            Event.Decrement -> emit(state.copy(value = state.value - 1))
        }
    }
}
