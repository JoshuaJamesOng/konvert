package com.ongtonnesoup.konvert.di

import androidx.navigation.fragment.NavHostFragment
import com.ongtonnesoup.konvert.MainActivity
import com.ongtonnesoup.konvert.detection.di.DetectionComponent
import com.ongtonnesoup.konvert.detection.di.MobileVisionModule
import com.ongtonnesoup.konvert.di.scopes.PerAppForegroundProcess
import dagger.Subcomponent

@PerAppForegroundProcess
@Subcomponent(
    modules = [
        DataSourcesModule::class,
        ClientModule::class,
        FragmentBindingModule::class,
        UpdateModule::class
    ]
)
interface ApplicationComponent {

    fun inject(activity: MainActivity)

    fun inject(hostFragment: NavHostFragment)

    fun getDetectionComponent(module: MobileVisionModule): DetectionComponent

    // Not a typo. Dagger's generated code does not fully quality it's `Provider` import
    interface Providerr {
        fun get(): ApplicationComponent
    }
}
