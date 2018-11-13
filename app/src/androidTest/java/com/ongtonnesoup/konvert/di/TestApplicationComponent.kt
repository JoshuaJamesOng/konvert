package com.ongtonnesoup.konvert.currency.di

import com.ongtonnesoup.konvert.currency.UpdateExchangeRates
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.di.ClientModule
import com.ongtonnesoup.konvert.di.SchedulerModule
import com.ongtonnesoup.konvert.di.TestDataSourcesModule
import com.ongtonnesoup.konvert.di.scopes.PerAppForegroundProcess
import dagger.Subcomponent

@PerAppForegroundProcess
@Subcomponent(modules = [
    TestDataSourcesModule::class,
    SchedulerModule::class,
    ClientModule::class
])
interface TestApplicationComponent {

    fun updateExchangeRates(): UpdateExchangeRates

    fun appDatabase(): AppDatabase
}
