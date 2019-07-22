package com.ongtonnesoup.konvert.currency

import arrow.core.Try
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import javax.inject.Inject
import javax.inject.Named

class GetLatestExchangeRates @Inject constructor(
    @Named("network") private val network: ExchangeRepository
) {
    suspend fun getExchangeRates(): Try<ExchangeRepository.ExchangeRates> {
        return network.getExchangeRates()
    }
}
