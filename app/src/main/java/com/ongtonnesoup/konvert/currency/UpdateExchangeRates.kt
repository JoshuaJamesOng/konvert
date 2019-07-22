package com.ongtonnesoup.konvert.currency

import androidx.annotation.CheckResult
import javax.inject.Inject

class UpdateExchangeRates @Inject constructor(
    private val getLatestExchangeRates: GetLatestExchangeRates,
    private val saveExchangeRates: SaveExchangeRates
) {
    @CheckResult
    suspend fun getExchangeRates(): Boolean {
        return getLatestExchangeRates.getExchangeRates()
                .fold({ false }, {
                    saveExchangeRates.save(it)
                    true
                })
    }
}
