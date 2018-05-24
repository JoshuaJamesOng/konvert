package com.ongtonnesoup.konvert.currency.data.network

import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import com.ongtonnesoup.konvert.isExpectedNetworkException
import io.reactivex.Completable
import io.reactivex.Single

class FixerIoExchangeRepository(private val client: FixerIoClient,
                                private val fromNetworkMapper: (FixerIoClient.Response) ->
                                            ExchangeRepository.ExchangeRates) : ExchangeRepository {

    override fun getExchangeRates(): Single<ExchangeRepository.ExchangeRates> {
        return client.getLatest(BASE_CURRENCY)
                .map { response ->
                    Timber.d { "Mapping network model to domain model on ${Thread.currentThread()}" }
                    fromNetworkMapper.invoke(response)
                }
                .onErrorReturn {
                    Timber.e { "Error getting rates from network on ${Thread.currentThread()}" }

                    if (it.isExpectedNetworkException()) {
                        ExchangeRepository.NO_DATA
                    } else {
                        throw it
                    }
                }
    }

    override fun putExchangeRates(rates: ExchangeRepository.ExchangeRates): Completable {
        return Completable.error(UnsupportedOperationException())
    }

    companion object {
        private val BASE_CURRENCY = "GBP"
    }

}