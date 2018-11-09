package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.state.State
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object StateModule {

    @Provides
    @JvmStatic
    @Named("defaultState")
    fun provideDefaultState() = State()
}