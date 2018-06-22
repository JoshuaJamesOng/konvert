package com.ongtonnesoup.konvert.currency.di

import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.currency.refresh.RefreshExchangeRatesWorker
import dagger.Subcomponent

@Subcomponent(modules = arrayOf(TestInitialisationModule::class, TestJobModule::class))
interface TestUpdateExchangeRatesComponent {

    fun inject(target: RefreshExchangeRatesWorker)

    fun appDatabase(): AppDatabase

}
