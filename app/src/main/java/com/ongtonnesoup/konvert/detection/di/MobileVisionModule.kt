package com.ongtonnesoup.konvert.detection.di

import com.ongtonnesoup.konvert.detection.DetectionViewModel
import com.ongtonnesoup.konvert.detection.OcrGateway
import com.ongtonnesoup.konvert.detection.mobilevision.MobileVisionOcrGateway
import dagger.Module
import dagger.Provides

@Module
class MobileVisionModule(private val vm: DetectionViewModel) {

    @Provides
    fun provideGateway(gateway: MobileVisionOcrGateway): OcrGateway {
        return gateway
    }

    @Provides
    fun provideOcrView(): MobileVisionOcrGateway.View {
        return vm
    }
}
