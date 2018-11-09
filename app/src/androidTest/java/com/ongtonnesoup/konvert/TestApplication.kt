package com.ongtonnesoup.konvert

import android.app.Application
import android.os.StrictMode
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

    override fun onCreate() {
        super.onCreate()

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        processComponent = DaggerTestProcessComponent.builder()
                .processModule(ProcessModule(this))
                .build()
    }

    override fun inject(target: RefreshExchangeRatesWorker) {
        workerComponent.inject(target)
    }
}
