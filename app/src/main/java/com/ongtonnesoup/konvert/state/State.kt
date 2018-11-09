package com.ongtonnesoup.konvert.state

data class State(
        val initialisationState: InitialisationState = InitialisationState.NOT_INITIALISED,
        val dataState: DataState = DataState.UNKNOWN,
        val refreshState: RefreshState = RefreshState.UNKNOWN
)

enum class InitialisationState {
    NOT_INITIALISED,
    INITIALISE,
    INITIALISING,
    INITIALISED
}

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
