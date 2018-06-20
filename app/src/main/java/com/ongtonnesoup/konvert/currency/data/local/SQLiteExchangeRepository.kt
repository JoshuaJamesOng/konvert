package com.ongtonnesoup.konvert.currency.data.local

import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import io.reactivex.Completable
import io.reactivex.Single

class SQLiteExchangeRepository(private val dao: ExchangeRatesDao,
                               private val fromDomainMapper: (ExchangeRepository.ExchangeRates) ->
                                            List<ExchangeRatesDao.ExchangeRate>,
                               private val fromLocalMapper: (List<ExchangeRatesDao.ExchangeRate>) ->
                                            ExchangeRepository.ExchangeRates) : ExchangeRepository {

    override fun getExchangeRates(): Single<ExchangeRepository.ExchangeRates> {
        Timber.d { "Creating network observable" }
        return dao.getAll()
                .limit(1)
                .map {
                    Timber.d { "Mapping database model to domain model" }
                    val mapped = fromLocalMapper.invoke(it)
                    Timber.d { "Mapped model $mapped" }
                    mapped
                }
                .single(ExchangeRepository.NO_DATA)
                .onErrorReturn { ExchangeRepository.NO_DATA }
    }

    override fun putExchangeRates(rates: ExchangeRepository.ExchangeRates): Completable {
        Timber.d { "Creating local observable" }
        return Completable.fromAction {
            // TODO We shouldn't clear exchange rates on errors
            Timber.d { "Inserting ${rates.rates.size} rates into DB on ${Thread.currentThread()}" }
            dao.clear()
            fromDomainMapper.invoke(rates).forEach {
                Timber.d { "Inserting $it into DB" }
                val result = dao.insert(it)
                Timber.d { "Inserted at: $result" }
            }
        }
    }

}
