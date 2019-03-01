package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.MainActivity
import com.ongtonnesoup.konvert.android.FragmentFactoryNavHostFragment
import com.ongtonnesoup.konvert.detection.di.DetectionComponent
import com.ongtonnesoup.konvert.detection.di.MobileVisionModule
import com.ongtonnesoup.konvert.di.scopes.PerAppForegroundProcess
import dagger.Subcomponent

@PerAppForegroundProcess
@Subcomponent(modules = [
    DataSourcesModule::class,
    ClientModule::class,
    FragmentBindingModule::class
])
interface ApplicationComponent {

    fun inject(activity: MainActivity)

    fun inject(hostFragment: FragmentFactoryNavHostFragment)

    fun getDetectionComponent(module: MobileVisionModule): DetectionComponent
}
