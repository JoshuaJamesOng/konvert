package com.ongtonnesoup.konvert.detection.di

import android.content.Context
import com.ongtonnesoup.konvert.detection.DetectionFragment
import com.ongtonnesoup.konvert.detection.OcrGateway
import com.ongtonnesoup.konvert.detection.mobilevision.MobileVisionOcrGateway
import com.ongtonnesoup.konvert.di.qualifiers.ContextType
import com.ongtonnesoup.konvert.di.qualifiers.Type
import dagger.Module
import dagger.Provides

@Module
class MobileVisionModule(private val fragment: DetectionFragment) {

    @Provides
    fun provideGateway(gateway: MobileVisionOcrGateway): OcrGateway {
        return gateway
    }

    @ContextType(Type.ACTIVITY)
    @Provides
    fun provideContext(): Context {
        return fragment.requireContext()
    }
}
