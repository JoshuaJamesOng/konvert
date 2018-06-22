package com.ongtonnesoup.konvert.currency

import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.RefreshState
import com.ongtonnesoup.konvert.state.updateRefreshState
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named

class GetLatestExchangeRates @Inject constructor(
        @Named("network") private val network: ExchangeRepository,
        private val appState: AppState
) {

    fun getExchangeRates(): Single<ExchangeRepository.ExchangeRates> {
        return network.getExchangeRates()
                .doOnSubscribe { updateRefreshState(appState, RefreshState.REFRESHING) }
    }
}