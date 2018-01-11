package com.ongtonnesoup.konvert.currency.network

import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.currency.ExchangeRepository
import io.reactivex.Completable
import io.reactivex.Single

class FixerIoExchangeRepository(private val client: FixerIoClient,
                                private val networkToDomainMapper: (FixerIoClient.FixerIoResponse) -> ExchangeRepository.ExchangeRates) : ExchangeRepository {

    override fun getExchangeRates(): Single<ExchangeRepository.ExchangeRates> {
        return client.getLatest(BASE_CURRENCY)
                .map { response ->
                    Timber.d { "Mapping network model to domain model" }
                    networkToDomainMapper.invoke(response)
                }
                .onErrorReturn { ExchangeRepository.ExchangeRates(emptyList()) }
    }

    override fun putExchangeRates(rates: ExchangeRepository.ExchangeRates): Completable {
        return Completable.error(UnsupportedOperationException())
    }

    companion object {
        private val BASE_CURRENCY = "GBP"
    }

}