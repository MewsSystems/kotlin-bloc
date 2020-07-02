package com.mews.app.bloc

import kotlinx.coroutines.flow.Flow

interface Bloc<EVENT : Any, STATE : Any> : Flow<STATE>, Sink<EVENT> {
    val state: STATE

    override suspend fun add(event: EVENT)

    fun addAsync(event: EVENT)
}
