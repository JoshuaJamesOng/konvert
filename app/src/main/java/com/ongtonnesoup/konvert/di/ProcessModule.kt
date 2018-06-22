package com.ongtonnesoup.konvert.di

import android.content.Context
import com.ongtonnesoup.konvert.InteractorSchedulers
import com.ongtonnesoup.konvert.Schedulers
import com.ongtonnesoup.konvert.di.scopes.PerProcess
import dagger.Module
import dagger.Provides
import timber.log.Timber

@PerProcess
@Module
class ProcessModule(private val context: Context) {

    init {
        // TODO Don't do this for release
        Timber.plant(Timber.DebugTree())
    }

    @PerProcess
    @Provides
    fun provideContext() = context

    @PerProcess
    @Provides
    fun provideSchedulers(): Schedulers = InteractorSchedulers()

}
