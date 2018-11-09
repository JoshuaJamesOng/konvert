package com.ongtonnesoup.konvert.currency

import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import javax.inject.Inject
import javax.inject.Named

class SaveExchangeRates @Inject constructor(
        @Named("local") private val local: ExchangeRepository
) {

    suspend fun save(exchangeRates: ExchangeRepository.ExchangeRates) {
        local.putExchangeRates(exchangeRates)
    }
}
