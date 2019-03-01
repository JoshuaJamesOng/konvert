package com.ongtonnesoup.konvert.currency.domain

import arrow.core.Try
import java.util.Collections.emptyList

interface ExchangeRepository {

    class NoDataException : Exception()

    companion object {
        val NO_DATA = ExchangeRates(emptyList())
    }

    suspend fun getExchangeRates(): Try<ExchangeRates>

    suspend fun putExchangeRates(rates: ExchangeRates)

    data class ExchangeRates(
            val rates: List<ExchangeRate>
    )

    data class ExchangeRate(
            val currency: String,
            val rate: Double
    )
}
