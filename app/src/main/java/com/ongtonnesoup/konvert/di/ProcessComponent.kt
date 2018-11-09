package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.di.scopes.PerProcess
import dagger.Component

@PerProcess
@Component(modules = [
    ProcessModule::class,
    StateModule::class,
    NetworkModule::class,
    DatabaseModule::class
])
interface ProcessComponent {

    fun getApplicationComponent(): ApplicationComponent

    fun getWorkerComponent(): WorkerComponent
}