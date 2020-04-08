package com.ongtonnesoup.konvert.currency.data.fixed

import arrow.core.Try
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository

class FixedExchangeRepository : ExchangeRepository {

    override suspend fun getExchangeRates(): Try<ExchangeRepository.ExchangeRates> {
        val rates = ExchangeRepository.ExchangeRates(
            listOf(
                ExchangeRepository.ExchangeRate("GBP", 1.00),
                ExchangeRepository.ExchangeRate("EUR", 1.50),
                ExchangeRepository.ExchangeRate("USD", 2.00)
            )
        )
        return Try.just(rates)
    }

    override suspend fun putExchangeRates(rates: ExchangeRepository.ExchangeRates) {
        throw UnsupportedOperationException()
    }
}
