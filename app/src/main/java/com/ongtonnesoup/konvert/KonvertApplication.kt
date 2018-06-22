package com.ongtonnesoup.konvert

import android.app.Application
import com.ongtonnesoup.konvert.currency.refresh.RefreshExchangeRatesWorker
import com.ongtonnesoup.konvert.di.*
import com.ongtonnesoup.konvert.initialisation.InitialiseApp
import com.ongtonnesoup.konvert.state.AppState
import javax.inject.Inject
import javax.inject.Provider

class KonvertApplication : Application(), Provider<ApplicationComponent>, Injector<RefreshExchangeRatesWorker> {

    @Inject
    lateinit var appState: AppState

    @Inject
    lateinit var initialiseApp: InitialiseApp

    @Inject
    lateinit var schedulers: Schedulers

    private lateinit var processComponent: ProcessComponent

    private var applicationComponent: ApplicationComponent? = null

    override fun onCreate() {
        super.onCreate()

        processComponent = DaggerProcessComponent.builder()
                .processModule(ProcessModule(this))
                .build()

        appState.updates()
                .filter { state -> !state.initialised }
                .flatMapCompletable { initialiseApp.initialise() }
                .subscribeOn(schedulers.getWorkerScheduler())
                .observeOn(schedulers.getPostExecutionScheduler())
                .subscribe()
    }

    // TODO Hook this up to process lifecycle
    fun onAppForegrounded() {
        if (applicationComponent == null) {
            applicationComponent = processComponent.getApplicationComponent()
        }
    }

    override fun get() = applicationComponent

    override fun inject(target: RefreshExchangeRatesWorker) {
        processComponent.getWorkerComponent().inject(target)
    }

}
