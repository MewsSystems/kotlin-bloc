package com.mews.app.bloc.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mews.app.bloc.BaseBloc
import com.mews.app.bloc.Bloc
import com.mews.app.bloc.Sink
import com.mews.app.bloc.Transition
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

abstract class BlocViewModel<EVENT : Any, STATE : Any> : ViewModel(), Bloc<EVENT, STATE> {
    abstract val initialState: STATE

    override fun emitAsync(event: EVENT) = bloc.emitAsync(event)

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<STATE>) = bloc.collect(collector)

    override suspend fun add(value: EVENT) = bloc.add(value)

    override val state: STATE get() = bloc.state

    private val bloc = object : BaseBloc<EVENT, STATE>(viewModelScope) {
        override val initialState: STATE by lazy { this@BlocViewModel.initialState }

        override suspend fun Sink<STATE>.mapEventToState(event: EVENT) {
            this@BlocViewModel.apply { mapEventToState(event) }
        }

        override suspend fun onTransition(transition: Transition<EVENT, STATE>) =
            this@BlocViewModel.onTransition(transition)

        override suspend fun onError(error: Throwable) = this@BlocViewModel.onError(error)

        override suspend fun onEvent(event: EVENT) = this@BlocViewModel.onEvent(event)

        override fun transformEvents(events: Flow<EVENT>): Flow<EVENT> = this@BlocViewModel.transformEvents(events)

        override fun transformTransitions(transitions: Flow<Transition<EVENT, STATE>>): Flow<Transition<EVENT, STATE>> =
            this@BlocViewModel.transformTransitions(transitions)
    }
}
