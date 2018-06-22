package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.default
import com.ongtonnesoup.konvert.di.scopes.PerProcess
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

@PerProcess
@Module
object NetworkModule {

    @PerProcess
    @Provides
    @JvmStatic
    fun provideOkHttpClient() = OkHttpClient.Builder().default()

}
