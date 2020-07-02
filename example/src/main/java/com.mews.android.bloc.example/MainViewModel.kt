package com.mews.android.bloc.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mews.android.bloc.BaseBloc
import com.mews.android.bloc.Bloc
import com.mews.android.bloc.Transition
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector

class MainViewModel : BlocViewModel<Event, State>() {
    override val initialState: State = State()

    override suspend fun FlowCollector<State>.mapEventToState(event: Event) {
        when (event) {
            Event.Increment -> emit(state.copy(value = state.value + 1))
            Event.Decrement -> emit(state.copy(value = state.value - 1))
        }
    }
}

abstract class BlocViewModel<EVENT : Any, STATE : Any> : ViewModel(), Bloc<EVENT, STATE> {
    abstract val initialState: STATE

    protected abstract suspend fun FlowCollector<STATE>.mapEventToState(event: EVENT)

    protected open suspend fun onTransition(transition: Transition<EVENT, STATE>) {}

    protected open suspend fun onError(error: Throwable) {}

    protected open suspend fun onEvent(event: EVENT) {}

    override fun addAsync(event: EVENT) {
        bloc.addAsync(event)
    }

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<STATE>) {
        return bloc.collect(collector)
    }

    override suspend fun add(event: EVENT) {
        bloc.add(event)
    }

    override val state: STATE get() = bloc.state

    private val bloc = object : BaseBloc<EVENT, STATE>(viewModelScope) {
        override val initialState: STATE by lazy { this@BlocViewModel.initialState }

        override suspend fun FlowCollector<STATE>.mapEventToState(event: EVENT) {
            this@BlocViewModel.apply { mapEventToState(event) }
        }

        override suspend fun onTransition(transition: Transition<EVENT, STATE>) {
            this@BlocViewModel.onTransition(transition)
        }

        override suspend fun onError(error: Throwable) {
            this@BlocViewModel.onError(error)
        }

        override suspend fun onEvent(event: EVENT) {
            this@BlocViewModel.onEvent(event)
        }
    }
}
