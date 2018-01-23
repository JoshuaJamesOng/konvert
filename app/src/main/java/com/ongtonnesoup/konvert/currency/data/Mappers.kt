package com.ongtonnesoup.konvert.currency.data

import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import com.ongtonnesoup.konvert.currency.data.local.ExchangeRatesDao
import com.ongtonnesoup.konvert.currency.data.network.FixerIoClient

fun domainToLocalMapper(): (ExchangeRepository.ExchangeRates) -> List<ExchangeRatesDao.ExchangeRate> = { domain ->
    domain.rates.map { ExchangeRatesDao.ExchangeRate(it.currency, it.rate) }
}

fun localToDomainMapper(): (List<ExchangeRatesDao.ExchangeRate>) -> ExchangeRepository.ExchangeRates = { local ->
    ExchangeRepository.ExchangeRates(local.map { ExchangeRepository.ExchangeRate(it.currency, it.rate) })
}

fun networkToDomainMapper(): (FixerIoClient.Response) -> ExchangeRepository.ExchangeRates = { network ->
    ExchangeRepository.ExchangeRates(network.rates.map { ExchangeRepository.ExchangeRate(it.key, it.value) })
}
