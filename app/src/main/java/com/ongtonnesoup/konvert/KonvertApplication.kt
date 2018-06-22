package com.ongtonnesoup.konvert

import android.app.Application
import com.ongtonnesoup.konvert.currency.refresh.RefreshExchangeRatesWorker
import com.ongtonnesoup.konvert.di.ApplicationComponent
import com.ongtonnesoup.konvert.di.ApplicationModule
import com.ongtonnesoup.konvert.di.DaggerApplicationComponent
import com.ongtonnesoup.konvert.di.Injector
import com.ongtonnesoup.konvert.initialisation.InitialiseApp
import com.ongtonnesoup.konvert.state.AppState
import javax.inject.Inject
import javax.inject.Provider

class KonvertApplication : Application(), Provider<ApplicationComponent>, Injector<RefreshExchangeRatesWorker> {
    private lateinit var applicationComponent: ApplicationComponent

    @Inject
    lateinit var appState: AppState

    @Inject
    lateinit var initialiseApp: InitialiseApp

    @Inject
    lateinit var schedulers: Schedulers

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()

        appState.updates()
                .filter { state -> !state.initialised }
                .flatMapCompletable { initialiseApp.initialise() }
                .subscribeOn(schedulers.getWorkerScheduler())
                .observeOn(schedulers.getPostExecutionScheduler())
                .subscribe()
    }

    override fun get() = applicationComponent

    override fun inject(target: RefreshExchangeRatesWorker) {
        applicationComponent.getUpdateExchangeRatesComponent().inject(target)
    }

}
