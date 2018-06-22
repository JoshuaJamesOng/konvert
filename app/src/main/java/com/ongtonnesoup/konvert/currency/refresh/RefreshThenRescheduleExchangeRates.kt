package com.ongtonnesoup.konvert.currency.refresh

import com.ongtonnesoup.konvert.currency.domain.UpdateExchangeRates
import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.RefreshState
import com.ongtonnesoup.konvert.state.updateRefreshState
import io.reactivex.Completable
import javax.inject.Inject

class RefreshThenRescheduleExchangeRates @Inject constructor(
        private val updateExchangeRates: UpdateExchangeRates,
        private val appState: AppState
) {

    fun refreshThenReschedule(): Completable {
        return updateExchangeRates.getExchangeRates()
                .doOnComplete { updateRefreshState(appState, RefreshState.SCHEDULED) }
    }

}