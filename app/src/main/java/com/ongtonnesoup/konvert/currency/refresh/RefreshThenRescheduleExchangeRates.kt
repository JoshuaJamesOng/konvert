package com.ongtonnesoup.konvert.currency.refresh

import com.ongtonnesoup.konvert.currency.UpdateExchangeRates
import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.RefreshState
import com.ongtonnesoup.konvert.state.updateRefreshState
import javax.inject.Inject

class RefreshThenRescheduleExchangeRates @Inject constructor(
        private val updateExchangeRates: UpdateExchangeRates,
        private val appState: AppState
) {

    suspend fun refreshThenReschedule() {
        updateExchangeRates.getExchangeRates()
        updateRefreshState(appState, RefreshState.SCHEDULED)
    }
}