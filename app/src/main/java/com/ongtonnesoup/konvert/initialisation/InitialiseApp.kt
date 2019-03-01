package com.ongtonnesoup.konvert.initialisation

import com.ongtonnesoup.konvert.currency.UpdateExchangeRates
import com.ongtonnesoup.konvert.currency.domain.GetCurrentDataState
import com.ongtonnesoup.konvert.currency.refresh.ScheduleRefresh
import com.ongtonnesoup.konvert.state.*
import javax.inject.Inject

class InitialiseApp @Inject constructor(
        private val getCurrentDataState: GetCurrentDataState,
        private val updateExchangeRates: UpdateExchangeRates,
        private val appState: AppState
) {

    suspend fun initialise() {
        updateInitialisedState(appState, InitialisationState.INITIALISING)

        when (getLocalDataState()) {
            DataState.NO_DATA -> fetchNowThenScheduleRefresh()
            DataState.CACHED_DATA -> scheduleRefresh()
            else -> {
                throw java.lang.IllegalStateException()
            }
        }

        updateInitialisedState(appState, InitialisationState.INITIALISED)
    }

    private suspend fun getLocalDataState(): DataState {
        return getCurrentDataState.load()
    }

    private suspend fun fetchNowThenScheduleRefresh() {
        updateRefreshState(appState, RefreshState.REFRESHING)
        val updated = updateExchangeRates.getExchangeRates()
        if (updated) {
            updateDataState(appState, DataState.CACHED_DATA)
        } else {
            updateDataState(appState, DataState.NO_DATA)
        }
        scheduleRefresh()
    }

    private fun scheduleRefresh() {
//        scheduleRefresh.scheduleRefresh()
        updateRefreshState(appState, RefreshState.SCHEDULED)
    }
}
