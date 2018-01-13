package com.ongtonnesoup.konvert.currency.local

import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.currency.ExchangeRepository
import io.reactivex.Completable
import io.reactivex.Single

class SQLiteExchangeRepository(private val dao: ExchangeRatesDao,
                               private val domainToLocalMapper: (ExchangeRepository.ExchangeRates) -> List<ExchangeRatesDao.ExchangeRate>,
                               private val localToDomainMapper: (List<ExchangeRatesDao.ExchangeRate>) -> ExchangeRepository.ExchangeRates) : ExchangeRepository {

    override fun getExchangeRates(): Single<ExchangeRepository.ExchangeRates> {
        Timber.d { "Creating network observable" }
        return dao.getAll()
                .map {
                    Timber.d { "Mapping database model to domain model" }
                    localToDomainMapper.invoke(it)
                }
                .single(ExchangeRepository.ExchangeRates(emptyList()))
                .onErrorReturn { ExchangeRepository.ExchangeRates(emptyList()) }
    }

    override fun putExchangeRates(rates: ExchangeRepository.ExchangeRates): Completable {
        Timber.d { "Creating local observable" }
        return Completable.fromAction {
            Timber.d { "Inserting ${rates.rates.size} rates into DB on ${Thread.currentThread()}" }
            dao.clear()
            domainToLocalMapper.invoke(rates).forEach {
                Timber.d { "Inserting $it into DB" }
                dao.insert(it)
            }
        }
    }

}