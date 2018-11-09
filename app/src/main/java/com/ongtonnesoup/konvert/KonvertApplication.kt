package com.ongtonnesoup.konvert

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.ongtonnesoup.konvert.common.Dispatchers
import com.ongtonnesoup.konvert.currency.refresh.RefreshExchangeRatesWorker
import com.ongtonnesoup.konvert.di.ApplicationComponent
import com.ongtonnesoup.konvert.di.DaggerProcessComponent
import com.ongtonnesoup.konvert.di.Injector
import com.ongtonnesoup.konvert.di.ProcessComponent
import com.ongtonnesoup.konvert.di.ProcessModule
import com.ongtonnesoup.konvert.initialisation.InitialiseApp
import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.InitialisationState
import com.ongtonnesoup.konvert.state.updateInitialisedState
import io.reactivex.Observable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    lateinit var dispatchers: Dispatchers

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
        listenForInitialisationRequired(appState)
                .doOnNext {
                    GlobalScope.launch {
                        withContext(dispatchers.execution) {
                            initialiseApp.initialise()
                        }
                    }
                }
                .subscribe()

        updateInitialisedState(appState, InitialisationState.INITIALISE)
    }

    override fun get() = applicationComponent

    override fun inject(target: RefreshExchangeRatesWorker) {
        processComponent.getWorkerComponent().inject(target)
    }
}

private fun listenForInitialisationRequired(appState: AppState): Observable<InitialisationState> {
    return appState.updates()
            .doOnSubscribe { }
            .map { state -> state.initialisationState }
            .filter { initialisationState -> initialisationState == InitialisationState.INITIALISE }
            .take(1)
}
