package com.ongtonnesoup.konvert.initialisation

import com.ongtonnesoup.konvert.currency.UpdateExchangeRates
import com.ongtonnesoup.konvert.currency.domain.GetCurrentDataState
import com.ongtonnesoup.konvert.currency.refresh.ScheduleRefresh
import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.DataState
import com.ongtonnesoup.konvert.state.InitialisationState
import com.ongtonnesoup.konvert.state.RefreshState
import com.ongtonnesoup.konvert.state.updateDataState
import com.ongtonnesoup.konvert.state.updateInitialisedState
import com.ongtonnesoup.konvert.state.updateRefreshState
import javax.inject.Inject

class InitialiseApp @Inject constructor(
        private val getCurrentDataState: GetCurrentDataState,
        private val updateExchangeRates: UpdateExchangeRates,
        private val scheduleRefresh: ScheduleRefresh,
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
        updateExchangeRates.getExchangeRates()
        updateDataState(appState, DataState.CACHED_DATA)
        scheduleRefresh()
    }

    private fun scheduleRefresh() {
        scheduleRefresh.scheduleRefresh()
        updateRefreshState(appState, RefreshState.SCHEDULED)
    }
}