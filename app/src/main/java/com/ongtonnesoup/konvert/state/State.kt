package com.ongtonnesoup.konvert.state

data class State(
        val dataState: DataState = DataState.UNKNOWN,
        val refreshState: RefreshState = RefreshState.UNKNOWN
)

enum class DataState {
    UNKNOWN,
    NO_DATA,
    CACHED_DATA
}

enum class RefreshState {
    UNKNOWN,
    SCHEDULED,
    REFRESHING
}
