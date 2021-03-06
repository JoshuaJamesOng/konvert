package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.InteractorSchedulers
import com.ongtonnesoup.konvert.Schedulers
import com.ongtonnesoup.konvert.di.scopes.PerProcess
import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.State
import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Named

@Module
object ProcessModule {

    init {
        // TODO Don't do this for release
        Timber.plant(Timber.DebugTree())
    }

    @PerProcess
    @Provides
    @JvmStatic
    fun provideSchedulers(): Schedulers = InteractorSchedulers()

    @Provides
    @JvmStatic
    fun provideAppState(@Named("defaultState") state: State): AppState = AppState(state)
}
