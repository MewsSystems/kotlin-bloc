package com.mews.app.bloc

interface Emitter<T> {
    suspend fun emit(value: T)
}
