package com.ongtonnesoup.konvert.currency.data.network

import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import com.ongtonnesoup.konvert.isExpectedNetworkException

class FixerIoExchangeRepository(private val client: FixerIoClient,
                                private val fromNetworkMapper: (FixerIoClient.Response) ->
                                ExchangeRepository.ExchangeRates,
                                private val configuration: Configuration) : ExchangeRepository {

    suspend override fun getExchangeRates(): ExchangeRepository.ExchangeRates {
        return try {
            val response = client.getLatest(BASE_CURRENCY, configuration.accessKey).await()
            fromNetworkMapper.invoke(response)
        } catch (e: Throwable) {
            if (e.isExpectedNetworkException()) {
                ExchangeRepository.NO_DATA
            } else {
                throw e
            }
        }
    }

    suspend override fun putExchangeRates(rates: ExchangeRepository.ExchangeRates) {
        UnsupportedOperationException()
    }

    companion object {
        private val BASE_CURRENCY = "EUR"
    }

    data class Configuration(val accessKey: String)
}
