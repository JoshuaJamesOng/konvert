package com.ongtonnesoup.konvert.currency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ongtonnesoup.konvert.common.Resource
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import javax.inject.Inject
import javax.inject.Named

class GetLatestExchangeRates @Inject constructor(
        @Named("network") private val network: ExchangeRepository
) {

    suspend fun getExchangeRates(): LiveData<Resource<ExchangeRepository.ExchangeRates>> {
        val result = MutableLiveData<Resource<ExchangeRepository.ExchangeRates>>()

        result.value = Resource.Loading()

        return runCatching {
            val exchangeRates = getNetworkExchangeRates()
            result.value = Resource.Success(exchangeRates)
            result
        }.getOrElse { e ->
            result.value = Resource.Error(e)
            result
        }
    }

    suspend fun getNetworkExchangeRates(): ExchangeRepository.ExchangeRates {
        return network.getExchangeRates()
    }
}
