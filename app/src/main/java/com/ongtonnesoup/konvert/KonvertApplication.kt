package com.ongtonnesoup.konvert

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LifecycleObserver
import com.ongtonnesoup.konvert.android.ActivityCallbacks
import com.ongtonnesoup.konvert.common.Dispatchers
import com.ongtonnesoup.konvert.currency.refresh.RefreshExchangeRatesWorker
import com.ongtonnesoup.konvert.di.*
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
        set(value) {
            if (field == null && value != null) {
                onApplicationComponentCreated(value)
                field = value
            } else {
                throw IllegalStateException()
            }
        }

    override fun onCreate() {
        super.onCreate()

        processComponent = createProcessComponent()

        createAppComponentOnFirstActivity()
    }

    private fun createProcessComponent(): ProcessComponent {
        return DaggerProcessComponent.builder()
                .processModule(ProcessModule(this))
                .build()
    }

    private fun createApplicationComponent() = processComponent.getApplicationComponent()

    private fun onApplicationComponentCreated(applicationComponent: ApplicationComponent) {
        applicationComponent.inject(this@KonvertApplication)

        initialiseApp()
    }

    private fun createAppComponentOnFirstActivity() {
        registerActivityLifecycleCallbacks(object : ActivityCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                unregisterActivityLifecycleCallbacks(this)

                applicationComponent = createApplicationComponent()

                initialiseApp()
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
