package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.currency.di.TestApplicationComponent
import com.ongtonnesoup.konvert.currency.di.TestNetworkModule
import com.ongtonnesoup.konvert.currency.di.TestWorkerComponent
import com.ongtonnesoup.konvert.di.scopes.PerProcess
import dagger.Component

@PerProcess
@Component(modules = [
    ProcessModule::class,
    StateModule::class,
    TestNetworkModule::class
])
interface TestProcessComponent {

    fun getApplicationComponent(): TestApplicationComponent

    fun getWorkerComponent(): TestWorkerComponent

}