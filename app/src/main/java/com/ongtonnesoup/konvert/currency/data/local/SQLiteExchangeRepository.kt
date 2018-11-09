package com.ongtonnesoup.konvert.currency.data.local

import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository

class SQLiteExchangeRepository(private val dao: ExchangeRatesDao,
                               private val fromDomainMapper: (ExchangeRepository.ExchangeRates) ->
                               List<ExchangeRatesDao.ExchangeRate>,
                               private val fromLocalMapper: (List<ExchangeRatesDao.ExchangeRate>) ->
                               ExchangeRepository.ExchangeRates) : ExchangeRepository {

    suspend override fun getExchangeRates(): ExchangeRepository.ExchangeRates {
        return try {
            val response = dao.getAll()
            fromLocalMapper.invoke(response)
        } catch (e: Exception) {
            ExchangeRepository.NO_DATA
        }
    }

    suspend override fun putExchangeRates(rates: ExchangeRepository.ExchangeRates) {
        Timber.d { "Creating local observable" }
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
