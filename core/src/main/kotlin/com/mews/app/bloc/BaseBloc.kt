package com.mews.app.bloc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseBloc<EVENT : Any, STATE : Any>(private val scope: CoroutineScope) : Bloc<EVENT, STATE> {
    protected abstract val initialState: STATE

    private val stateFlow by lazy { MutableStateFlow(initialState) }

    override val state: STATE get() = stateFlow.value

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<STATE>) = stateFlow.collect(collector)

    private val eventChannel by lazy {
        Channel<EVENT>().also { channel ->
            channel.consumeAsFlow()
                .let(::transformEvents)
                .flatMapConcat { event ->
                    channelFlow<STATE> { StateSink(this).mapEventToState(event) }
                        .map { Transition(stateFlow.value, event, it) }
                        .catch { doOnError(it) }
                        .let(::transformTransitions)
                }
                .onEach { transition ->
                    if (transition.currentState == transition.nextState) return@onEach
                    try {
                        doOnTransition(transition)
                        stateFlow.value = transition.nextState
                    } catch (e: Throwable) {
                        doOnError(e)
                    }
                }
                .launchIn(scope)
        }
    }

    override suspend fun add(value: EVENT) {
        try {
            doOnEvent(value)
            eventChannel.send(value)
        } catch (e: Throwable) {
            doOnError(e)
        }
    }

    override fun emitAsync(event: EVENT) {
        scope.launch { add(event) }
    }

    private suspend fun doOnEvent(event: EVENT) {
        BlocSupervisor.delegate?.onEvent(event)
        onEvent(event)
    }

    private suspend fun doOnTransition(transition: Transition<EVENT, STATE>) {
        BlocSupervisor.delegate?.onTransition(transition)
        onTransition(transition)
    }

    private suspend fun doOnError(error: Throwable) {
        BlocSupervisor.delegate?.onError(error)
        onError(error)
    }
}

private class StateSink<S>(private val producerScope: ProducerScope<S>) : Sink<S> {
    override suspend fun add(value: S) = producerScope.send(value)
}
