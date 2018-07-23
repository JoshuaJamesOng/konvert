package com.ongtonnesoup.konvert.currency.di

import com.ongtonnesoup.konvert.di.ClientModule
import com.ongtonnesoup.konvert.di.DataSourcesModule
import com.ongtonnesoup.konvert.di.SchedulerModule
import com.ongtonnesoup.konvert.di.scopes.PerAppForegroundProcess
import dagger.Subcomponent

@PerAppForegroundProcess
@Subcomponent(modules = [
    TestDatabaseModule::class,
    DataSourcesModule::class,
    SchedulerModule::class,
    ClientModule::class
])
interface TestApplicationComponent {

}
