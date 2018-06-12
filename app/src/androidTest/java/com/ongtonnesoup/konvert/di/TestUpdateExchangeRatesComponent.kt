package com.ongtonnesoup.konvert.currency.di

import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.currency.work.UpdateExchangeRatesWorker
import dagger.Subcomponent

@Subcomponent(modules = arrayOf(TestInitialisationModule::class, TestJobModule::class))
interface TestUpdateExchangeRatesComponent {

    fun inject(target: UpdateExchangeRatesWorker)

    fun appDatabase(): AppDatabase

}
