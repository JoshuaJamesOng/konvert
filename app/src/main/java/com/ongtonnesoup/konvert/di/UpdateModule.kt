package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.appupdate.AppUpdateGateway
import com.ongtonnesoup.konvert.appupdate.GooglePlayAppUpdater
import dagger.Binds
import dagger.Module

@Module
abstract class UpdateModule {

    @Binds
    abstract fun provideAppUpdateGateway(googlePlayAppUpdater: GooglePlayAppUpdater): AppUpdateGateway<GooglePlayAppUpdater.Updater>

    @Binds
    abstract fun provideUnknownAppUpdateGateway(googlePlayAppUpdater: GooglePlayAppUpdater): AppUpdateGateway<*>

}