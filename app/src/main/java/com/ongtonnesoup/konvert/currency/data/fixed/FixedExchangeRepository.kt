package com.ongtonnesoup.konvert.currency.data.fixed

import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository

class FixedExchangeRepository : ExchangeRepository {

    suspend override fun getExchangeRates(): ExchangeRepository.ExchangeRates {
        return ExchangeRepository.ExchangeRates(
                listOf(
                        ExchangeRepository.ExchangeRate("GBP", 1.00),
                        ExchangeRepository.ExchangeRate("EUR", 1.50),
                        ExchangeRepository.ExchangeRate("USD", 2.00)
                )
        )
    }

    suspend override fun putExchangeRates(rates: ExchangeRepository.ExchangeRates) {
        throw UnsupportedOperationException()
    }
}