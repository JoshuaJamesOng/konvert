package com.ongtonnesoup.konvert.state

fun updateInitialisedState(appState: AppState, initialised: Boolean) {
    appState.update(appState.current().copy(initialised = initialised))
}

fun updateDataState(appState: AppState, dataState: DataState) {
    appState.update(appState.current().copy(dataState = dataState))
}

fun updateRefreshState(appState: AppState, refreshState: RefreshState) {
    appState.update(appState.current().copy(refreshState = refreshState))
}