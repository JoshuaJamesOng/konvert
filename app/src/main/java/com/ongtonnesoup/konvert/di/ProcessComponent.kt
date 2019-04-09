package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.KonvertApplication
import com.ongtonnesoup.konvert.di.scopes.PerProcess
import dagger.Component

@PerProcess
@Component(modules = [
    ProcessModule::class,
    StateModule::class,
    NetworkModule::class,
    DatabaseModule::class,
    SchedulerModule::class
])
interface ProcessComponent {
    fun inject(application: KonvertApplication)

    fun getApplicationComponent(): ApplicationComponent

    fun getWorkerComponent(): WorkerComponent

    // Not a typo. Dagger's generated code does not fully quality it's `Provider` import
    interface Providerr {
        fun get(): ProcessComponent
    }
}
