package com.ongtonnesoup.konvert

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

fun OkHttpClient.Builder.default(): OkHttpClient {
    return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .build()
}

fun Retrofit.Builder.default(): Retrofit.Builder {
    return this
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .validateEagerly(true)
}

fun Throwable.isExpectedNetworkException() = this is HttpException || this is IOException