package com.ongtonnesoup.konvert.currency.di

import com.ongtonnesoup.konvert.default
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

@Module
object TestNetworkModule {

    @Provides
    @JvmStatic
    fun provideOkHttpClient() = OkHttpClient.Builder().default()
}
