package com.ongtonnesoup.konvert

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ProcessLifecycleOwner
import com.ongtonnesoup.konvert.currency.refresh.RefreshExchangeRatesWorker
import com.ongtonnesoup.konvert.di.*
import com.ongtonnesoup.konvert.initialisation.InitialiseApp
import com.ongtonnesoup.konvert.state.AppState
import javax.inject.Inject
import javax.inject.Provider

class KonvertApplication : Application(),
        Provider<ApplicationComponent>,
        Injector<RefreshExchangeRatesWorker>,
        LifecycleObserver {

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

        processComponent = createProcessComponent()

        createAppComponentOnForeground()

        initialiseApp()
    }

    private fun createProcessComponent(): ProcessComponent {
        return DaggerProcessComponent.builder()
                .processModule(ProcessModule(this))
                .build()
    }

    private fun createAppComponentOnForeground() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onAppForegrounded() {
                if (applicationComponent == null) {
                    applicationComponent = processComponent.getApplicationComponent()
                }
            }
        })
    }

    private fun initialiseApp() {
        appState.updates()
                .filter { state -> !state.initialised }
                .flatMapCompletable { initialiseApp.initialise() }
                .subscribeOn(schedulers.getWorkerScheduler())
                .observeOn(schedulers.getPostExecutionScheduler())
                .subscribe()
    }

    override fun get() = applicationComponent

    override fun inject(target: RefreshExchangeRatesWorker) {
        processComponent.getWorkerComponent().inject(target)
    }

}
