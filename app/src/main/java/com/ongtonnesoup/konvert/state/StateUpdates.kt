package com.ongtonnesoup.konvert.state

fun updateInitialisedState(appState: AppState, initialisationState: InitialisationState) {
    appState.update(appState.current().copy(initialisationState = initialisationState))
}

fun updateDataState(appState: AppState, dataState: DataState) {
    appState.update(appState.current().copy(dataState = dataState))
}

fun updateRefreshState(appState: AppState, refreshState: RefreshState) {
    appState.update(appState.current().copy(refreshState = refreshState))
}
