package com.ongtonnesoup.konvert.detection.di

import com.ongtonnesoup.konvert.detection.DetectionViewModel
import com.ongtonnesoup.konvert.di.scopes.PerFragment
import dagger.Subcomponent

@PerFragment
@Subcomponent(
    modules = [
        MobileVisionModule::class
    ]
)
interface DetectionComponent {

    fun inject(target: DetectionViewModel)
}
