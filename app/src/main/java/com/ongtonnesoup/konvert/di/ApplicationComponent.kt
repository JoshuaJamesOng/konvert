package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.KonvertApplication
import com.ongtonnesoup.konvert.detection.di.DetectionComponent
import com.ongtonnesoup.konvert.detection.di.MobileVisionModule
import com.ongtonnesoup.konvert.di.scopes.PerAppForegroundProcess
import dagger.Subcomponent

@PerAppForegroundProcess
@Subcomponent(modules = [
    DataSourcesModule::class,
    SchedulerModule::class,
    ClientModule::class
])
interface ApplicationComponent {

    fun inject(application: KonvertApplication)

    fun getDetectionComponent(module: MobileVisionModule) : DetectionComponent

}
