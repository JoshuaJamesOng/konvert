package com.ongtonnesoup.konvert

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentFactory
import com.ongtonnesoup.konvert.android.getProcessComponent
import com.ongtonnesoup.konvert.android.setFragmentManagers
import com.ongtonnesoup.konvert.common.Dispatchers
import com.ongtonnesoup.konvert.di.ApplicationComponent
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

class MainActivity : AppCompatActivity(), Provider<ApplicationComponent> {

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    @Inject
    lateinit var appState: AppState

    @Inject
    lateinit var initialiseApp: InitialiseApp

    @Inject
    lateinit var dispatchers: Dispatchers

    private val component: ApplicationComponent by lazy {
        getProcessComponent(this).getApplicationComponent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        setFragmentManagers(this, fragmentFactory)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        initialiseApp()
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

    private fun listenForInitialisationRequired(appState: AppState): Observable<InitialisationState> {
        return appState.updates()
                .doOnSubscribe { }
                .map { state -> state.initialisationState }
                .filter { initialisationState -> initialisationState == InitialisationState.INITIALISE }
                .take(1)
    }

    override fun get(): ApplicationComponent = component
}
