package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.default
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

@Module
object NetworkModule {

    @Provides
    @JvmStatic
    fun provideOkHttpClient() = OkHttpClient.Builder().default()

}
