package com.ongtonnesoup.konvert.currency.domain

import java.util.Collections.emptyList

interface ExchangeRepository {

    companion object {
        val NO_DATA = ExchangeRates(emptyList())
    }

    suspend fun getExchangeRates(): ExchangeRates

    suspend fun putExchangeRates(rates: ExchangeRates)

    data class ExchangeRates(
            val rates: List<ExchangeRate>
    )

    data class ExchangeRate(
            val currency: String,
            val rate: Double
    )
}
