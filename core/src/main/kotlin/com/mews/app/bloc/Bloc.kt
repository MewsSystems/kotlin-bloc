package com.mews.app.bloc

import kotlinx.coroutines.flow.Flow

interface Bloc<EVENT : Any, STATE : Any> : Flow<STATE>, Emitter<EVENT> {
    val state: STATE

    override suspend fun emit(value: EVENT)

    fun emitAsync(event: EVENT)
}
