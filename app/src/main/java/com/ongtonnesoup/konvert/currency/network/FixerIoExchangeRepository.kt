package com.ongtonnesoup.konvert.currency.network

import com.ongtonnesoup.konvert.currency.ExchangeRepository
import io.reactivex.Completable
import io.reactivex.Single

class FixerIoExchangeRepository(private val client: FixerIoClient,
                                private val networkToLocalMapper: (FixerIoClient.FixerIoResponse) -> ExchangeRepository.ExchangeRates) : ExchangeRepository {

    override fun getExchangeRates(): Single<ExchangeRepository.ExchangeRates> {
        return client.getLatest(BASE_CURRENCY)
                .map { response ->
                    networkToLocalMapper.invoke(response)
                }
    }

    override fun putExchangeRates(rates: ExchangeRepository.ExchangeRates): Completable {
        return Completable.error(UnsupportedOperationException())
    }

    companion object {
        private val BASE_CURRENCY = "GBP"
    }

}