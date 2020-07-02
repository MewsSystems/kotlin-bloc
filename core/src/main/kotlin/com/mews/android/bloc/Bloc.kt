package com.mews.android.bloc

import kotlinx.coroutines.flow.Flow

interface Bloc<EVENT : Any, STATE : Any> : Flow<STATE> {
    val state: STATE

    suspend fun add(event: EVENT)

    fun addAsync(event: EVENT)
}
