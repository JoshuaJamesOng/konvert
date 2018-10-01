package com.ongtonnesoup.konvert.currency.data.network

import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository

class FixerIoExchangeRepository(private val client: FixerIoClient,
                                private val fromNetworkMapper: (FixerIoClient.Response) ->
                                ExchangeRepository.ExchangeRates) : ExchangeRepository {

    override suspend fun getExchangeRates(): ExchangeRepository.ExchangeRates {
        val response = client.getLatest(BASE_CURRENCY).await()
        return fromNetworkMapper.invoke(response)
    }

    override suspend fun putExchangeRates(rates: ExchangeRepository.ExchangeRates) {
        UnsupportedOperationException()
    }

    companion object {
        private val BASE_CURRENCY = "GBP"
    }

}
