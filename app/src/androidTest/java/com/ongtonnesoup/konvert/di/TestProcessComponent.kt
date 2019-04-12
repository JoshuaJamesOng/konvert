package com.ongtonnesoup.konvert.di

import android.content.Context
import com.ongtonnesoup.konvert.currency.di.TestApplicationComponent
import com.ongtonnesoup.konvert.currency.di.TestDatabaseModule
import com.ongtonnesoup.konvert.currency.di.TestNetworkModule
import com.ongtonnesoup.konvert.currency.di.TestWorkerComponent
import com.ongtonnesoup.konvert.di.qualifiers.ContextType
import com.ongtonnesoup.konvert.di.qualifiers.Type
import com.ongtonnesoup.konvert.di.scopes.PerProcess
import dagger.BindsInstance
import dagger.Component

@PerProcess
@Component(modules = [
    ProcessModule::class,
    StateModule::class,
    TestNetworkModule::class,
    TestDatabaseModule::class
])
interface TestProcessComponent {

    fun getApplicationComponent(): TestApplicationComponent

    fun getWorkerComponent(): TestWorkerComponent

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance @PerProcess @ContextType(Type.APPLICATION) context: Context): TestProcessComponent
    }
}
