package com.ongtonnesoup.konvert.common

data class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun ifNotHandled(function: (T) -> Any) {
        if (!hasBeenHandled) {
            hasBeenHandled = true
            function.invoke(content)
        }
    }

    fun peekContent(): T = content
}
