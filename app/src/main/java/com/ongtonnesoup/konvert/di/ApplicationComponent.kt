package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.KonvertApplication
import dagger.Component

@Component(modules = arrayOf(ApplicationModule::class, NetworkModule::class, DatabaseModule::class))
interface ApplicationComponent {

    fun getUpdateExchangeRatesComponent(): UpdateExchangeRatesComponent

    fun inject(application: KonvertApplication)

}