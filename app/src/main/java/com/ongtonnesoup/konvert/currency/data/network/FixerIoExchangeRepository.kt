package com.ongtonnesoup.konvert.currency.data.network

import arrow.core.Try
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import com.ongtonnesoup.konvert.isExpectedNetworkException

class FixerIoExchangeRepository(private val client: FixerIoClient,
                                private val fromNetworkMapper: (FixerIoClient.Response) ->
                                ExchangeRepository.ExchangeRates) : ExchangeRepository {

    suspend override fun getExchangeRates(): Try<ExchangeRepository.ExchangeRates> {
        return runCatching {
            val response = client.getLatest(BASE_CURRENCY).await()
            Try.just(fromNetworkMapper.invoke(response))
        }.getOrElse { e ->
            if (e.isExpectedNetworkException()) {
                Try.raise(ExchangeRepository.NoDataException())
            } else {
                throw e
            }
        }
    }

    suspend override fun putExchangeRates(rates: ExchangeRepository.ExchangeRates) {
        UnsupportedOperationException()
    }

    companion object {
        private val BASE_CURRENCY = "GBP"
    }
}
