package com.ongtonnesoup.konvert.currency

import com.ongtonnesoup.konvert.currency.local.ExchangeRatesDao
import com.ongtonnesoup.konvert.currency.network.FixerIoClient

fun domainToLocalMapper(): (ExchangeRepository.ExchangeRates) -> List<ExchangeRatesDao.ExchangeRate> = { domain ->
    domain.rates.map { ExchangeRatesDao.ExchangeRate(it.currency, it.rate) }
}

fun localToDomainMapper(): (List<ExchangeRatesDao.ExchangeRate>) -> ExchangeRepository.ExchangeRates = { local ->
    ExchangeRepository.ExchangeRates(local.map { ExchangeRepository.ExchangeRate(it.currency, it.rate) })
}

fun networkToLocalMapper(): (FixerIoClient.FixerIoResponse) -> ExchangeRepository.ExchangeRates = { network ->
    ExchangeRepository.ExchangeRates(network.rates.map { ExchangeRepository.ExchangeRate(it.currency, it.rate) })
}