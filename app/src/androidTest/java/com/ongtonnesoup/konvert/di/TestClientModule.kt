package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.currency.data.network.FixerIoClient
import com.ongtonnesoup.konvert.default
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
object TestClientModule {

    @Provides
    @JvmStatic
    fun provideRetrofit(okHttpClient: OkHttpClient): FixerIoClient {
        return Retrofit.Builder()
                .default()
                .baseUrl("http://data.fixer.io/api/")
                .client(okHttpClient)
                .build()
                .create(FixerIoClient::class.java)
    }

}