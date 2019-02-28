package com.ongtonnesoup.konvert

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import com.ongtonnesoup.konvert.currency.refresh.RefreshExchangeRatesWorker
import com.ongtonnesoup.konvert.di.DaggerProcessComponent
import com.ongtonnesoup.konvert.di.Injector
import com.ongtonnesoup.konvert.di.ProcessComponent
import com.ongtonnesoup.konvert.di.ProcessModule
import javax.inject.Provider

class KonvertApplication : Application(),
        Provider<ProcessComponent>,
        Injector<RefreshExchangeRatesWorker>,
        LifecycleObserver {

    private lateinit var processComponent: ProcessComponent

    override fun onCreate() {
        super.onCreate()

        processComponent = createProcessComponent()
    }

    private fun createProcessComponent(): ProcessComponent {
        return DaggerProcessComponent.builder()
                .processModule(ProcessModule(this))
                .build()
    }

    override fun get() = processComponent

    override fun inject(target: RefreshExchangeRatesWorker) {
        processComponent.getWorkerComponent().inject(target)
    }
}
