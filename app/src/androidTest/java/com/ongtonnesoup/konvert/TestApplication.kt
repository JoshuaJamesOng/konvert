package com.ongtonnesoup.konvert

import android.app.Application
import android.os.StrictMode
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.currency.di.TestApplicationComponent
import com.ongtonnesoup.konvert.currency.di.TestWorkerComponent
import com.ongtonnesoup.konvert.currency.refresh.RefreshExchangeRatesWorker
import com.ongtonnesoup.konvert.di.DaggerTestProcessComponent
import com.ongtonnesoup.konvert.di.Injector
import com.ongtonnesoup.konvert.di.ProcessModule
import com.ongtonnesoup.konvert.di.TestProcessComponent

class TestApplication : Application(), Injector<RefreshExchangeRatesWorker> {

    private lateinit var processComponent: TestProcessComponent

    val workerComponent: TestWorkerComponent by lazy {
        processComponent.getWorkerComponent()
    }

    val appComponent: TestApplicationComponent by lazy {
        processComponent.getApplicationComponent()
    }

    override fun onCreate() {
        super.onCreate()

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        Timber.plant(timber.log.Timber.DebugTree())

        processComponent = DaggerTestProcessComponent.builder()
                .processModule(ProcessModule(this))
                .build()
    }

    override fun inject(target: RefreshExchangeRatesWorker) {
        workerComponent.inject(target)
    }
}
