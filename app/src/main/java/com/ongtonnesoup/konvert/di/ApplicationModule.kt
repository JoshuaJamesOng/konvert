package com.ongtonnesoup.konvert.di

import android.content.Context
import dagger.Module
import dagger.Provides
import timber.log.Timber

@Module
class ApplicationModule(private val context: Context) {

    init {
        Timber.plant(Timber.DebugTree())
    }

    @Provides
    fun provideContext() = context

}
