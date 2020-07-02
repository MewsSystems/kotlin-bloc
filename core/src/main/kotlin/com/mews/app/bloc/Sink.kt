package com.mews.app.bloc

interface Sink<EVENT> {
    suspend fun add(event: EVENT)
}
