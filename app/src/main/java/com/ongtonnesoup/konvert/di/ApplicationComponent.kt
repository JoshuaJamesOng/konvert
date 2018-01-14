package com.ongtonnesoup.konvert.di

import dagger.Component

@Component(modules = arrayOf(ApplicationModule::class, NetworkModule::class, DatabaseModule::class))
interface ApplicationComponent {

    fun getJobComponent(): JobComponent

}