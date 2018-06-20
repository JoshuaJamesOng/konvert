package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.KonvertApplication
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, NetworkModule::class, DatabaseModule::class))
interface ApplicationComponent {

    fun getUpdateExchangeRatesComponent(): UpdateExchangeRatesComponent

    fun inject(application: KonvertApplication)

}
