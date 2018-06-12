package com.ongtonnesoup.konvert.currency.di

import android.content.Context
import dagger.Module
import dagger.Provides
import timber.log.Timber

@Module
class TestApplicationModule(private val context: Context) {

    init {
        Timber.plant(Timber.DebugTree())
    }

    @Provides
    fun provideContext() = context

}
