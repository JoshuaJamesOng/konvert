package com.ongtonnesoup.konvert.currency

import android.app.Application
import com.ongtonnesoup.konvert.currency.di.DaggerTestApplicationComponent
import com.ongtonnesoup.konvert.currency.di.TestApplicationComponent
import com.ongtonnesoup.konvert.currency.di.TestApplicationModule
import com.ongtonnesoup.konvert.currency.di.TestUpdateExchangeRatesComponent
import com.ongtonnesoup.konvert.currency.work.UpdateExchangeRatesWorker
import com.ongtonnesoup.konvert.di.Injector

class TestApplication : Application(), Injector<UpdateExchangeRatesWorker> {
    private lateinit var applicationComponent: TestApplicationComponent

    val updateExchangeRatesComponent: TestUpdateExchangeRatesComponent by lazy {
        applicationComponent.getUpdateExchangeRatesComponent()
    }

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerTestApplicationComponent.builder()
                .testApplicationModule(TestApplicationModule(this))
                .build()
    }

    override fun inject(target: UpdateExchangeRatesWorker) {
        updateExchangeRatesComponent.inject(target)
    }

}