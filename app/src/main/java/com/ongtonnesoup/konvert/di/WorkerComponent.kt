package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.currency.refresh.RefreshExchangeRatesWorker
import dagger.Subcomponent

@Subcomponent(modules = [
    DataSourcesModule::class,
    ClientModule::class,
    DatabaseModule::class
])
interface WorkerComponent {

    fun inject(target: RefreshExchangeRatesWorker)

}
