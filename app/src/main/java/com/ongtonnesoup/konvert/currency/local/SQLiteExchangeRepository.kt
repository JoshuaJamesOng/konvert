package com.ongtonnesoup.konvert.currency.local

import com.ongtonnesoup.konvert.currency.ExchangeRepository
import io.reactivex.Completable
import io.reactivex.Single

class SQLiteExchangeRepository(private val dao: ExchangeRatesDao,
                               private val domainToLocalMapper: (ExchangeRepository.ExchangeRates) -> List<ExchangeRatesDao.ExchangeRate>,
                               private val localToDomainMapper: (List<ExchangeRatesDao.ExchangeRate>) -> ExchangeRepository.ExchangeRates) : ExchangeRepository {

    override fun getExchangeRates(): Single<ExchangeRepository.ExchangeRates> {
        return dao.getAll()
                .map { localToDomainMapper.invoke(it) }
                .single(ExchangeRepository.ExchangeRates(emptyList()))
    }

    override fun putExchangeRates(rates: ExchangeRepository.ExchangeRates): Completable {
        return Completable.fromAction {
            dao.clear()
            domainToLocalMapper.invoke(rates)
                    .forEach { dao.insert(it) }
        }
    }

}