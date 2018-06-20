package com.ongtonnesoup.konvert.currency.di

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(TestApplicationModule::class, TestNetworkModule::class, TestDatabaseModule::class))
interface TestApplicationComponent {

    fun getUpdateExchangeRatesComponent(): TestUpdateExchangeRatesComponent

}
