package com.ongtonnesoup.konvert.detection.di

import com.ongtonnesoup.konvert.detection.DetectionViewModelFactory
import com.ongtonnesoup.konvert.di.scopes.PerFragment
import dagger.Subcomponent

@PerFragment
@Subcomponent(modules = [
    MobileVisionModule::class
])
interface DetectionComponent {

    fun inject(target: DetectionViewModelFactory)
}
