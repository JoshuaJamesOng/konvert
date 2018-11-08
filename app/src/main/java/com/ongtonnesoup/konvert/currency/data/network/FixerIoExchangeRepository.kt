package com.ongtonnesoup.konvert.currency.data.network

import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import com.ongtonnesoup.konvert.isExpectedNetworkException

class FixerIoExchangeRepository(private val client: FixerIoClient,
                                private val fromNetworkMapper: (FixerIoClient.Response) ->
                                ExchangeRepository.ExchangeRates) : ExchangeRepository {

    override suspend fun getExchangeRates(): ExchangeRepository.ExchangeRates {
        return try {
            val response = client.getLatest(BASE_CURRENCY).await()
            fromNetworkMapper.invoke(response)
        } catch (e: Throwable) {
            if (e.isExpectedNetworkException()) {
                ExchangeRepository.NO_DATA
            } else {
                throw e
            }
        }
    }

    override suspend fun putExchangeRates(rates: ExchangeRepository.ExchangeRates) {
        UnsupportedOperationException()
    }

    companion object {
        private val BASE_CURRENCY = "GBP"
    }

}
