package com.ongtonnesoup.konvert

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import com.ongtonnesoup.konvert.currency.refresh.EnableBackgroundSync
import com.ongtonnesoup.konvert.currency.refresh.RefreshExchangeRatesWorker
import com.ongtonnesoup.konvert.di.DaggerProcessComponent
import com.ongtonnesoup.konvert.di.ProcessComponent
import javax.inject.Inject

class KonvertApplication : Application(),
    ProcessComponent.Providerr,
    RefreshExchangeRatesWorker.Injector,
    LifecycleObserver {

    @Inject
    lateinit var enableBackgroundSync: EnableBackgroundSync

    private lateinit var processComponent: ProcessComponent

    override fun onCreate() {
        super.onCreate()

        processComponent = createProcessComponent()
        processComponent.inject(this)

        enableBackgroundSync.enableBackgroundSync()
    }

    private fun createProcessComponent(): ProcessComponent {
        return DaggerProcessComponent.factory()
            .create(this)
    }

    override fun get() = processComponent

    override fun inject(target: RefreshExchangeRatesWorker) {
        processComponent.getWorkerComponent().inject(target)
    }
}
