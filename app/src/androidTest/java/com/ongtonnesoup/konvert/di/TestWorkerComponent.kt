package com.ongtonnesoup.konvert.currency.di

import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.currency.refresh.RefreshExchangeRatesWorker
import com.ongtonnesoup.konvert.di.TestClientModule
import com.ongtonnesoup.konvert.di.TestDataSourcesModule
import com.ongtonnesoup.konvert.di.scopes.PerWorker
import dagger.Subcomponent

@PerWorker
@Subcomponent(
    modules = [
        TestDataSourcesModule::class,
        TestClientModule::class
    ]
)
interface TestWorkerComponent {

    fun inject(target: RefreshExchangeRatesWorker)

    fun appDatabase(): AppDatabase
}
