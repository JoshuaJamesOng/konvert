package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.currency.refresh.RefreshExchangeRatesWorker
import com.ongtonnesoup.konvert.di.scopes.PerWorker
import dagger.Subcomponent

@PerWorker
@Subcomponent(
    modules = [
        DataSourcesModule::class,
        ClientModule::class
    ]
)
interface WorkerComponent {

    fun inject(target: RefreshExchangeRatesWorker)
}
