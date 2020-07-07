package com.mews.app.bloc

interface Sink<T> {
    suspend fun add(value: T)
}
