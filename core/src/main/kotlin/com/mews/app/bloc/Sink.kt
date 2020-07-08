package com.mews.app.bloc

interface Sink<T> {
    fun add(value: T)
}
