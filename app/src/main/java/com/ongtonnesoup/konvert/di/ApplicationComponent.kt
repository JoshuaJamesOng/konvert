package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.KonvertApplication
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ApplicationModule::class,
    NetworkModule::class,
    DatabaseModule::class,
    StateModule::class,
    DataSourcesModule::class
])
interface ApplicationComponent {

    fun getUpdateExchangeRatesComponent(): UpdateExchangeRatesComponent

    fun inject(application: KonvertApplication)

}
