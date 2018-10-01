package com.ongtonnesoup.konvert.currency

import javax.inject.Inject

class UpdateExchangeRates @Inject constructor(
        private val getLatestExchangeRates: GetLatestExchangeRates,
        private val saveExchangeRates: SaveExchangeRates
) {

    suspend fun getExchangeRates() {
        val exchangeRates = getLatestExchangeRates.getNetworkExchangeRates()
        saveExchangeRates.save(exchangeRates) // TODO Only save if valid response
    }

}
