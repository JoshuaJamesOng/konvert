package com.ongtonnesoup.konvert

import android.app.Application
import com.ongtonnesoup.konvert.currency.job.UpdateExchangeRatesJob

class KonvertApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        UpdateExchangeRatesJob.schedule(this)
    }
}