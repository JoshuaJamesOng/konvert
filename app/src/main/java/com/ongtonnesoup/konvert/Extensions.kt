package com.ongtonnesoup.konvert

import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.Worker
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

fun OkHttpClient.Builder.default(): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor())
        .build()
}

fun Retrofit.Builder.default(): Retrofit.Builder {
    return this
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .validateEagerly(true)
}

fun Throwable.isExpectedNetworkException() = this is HttpException || this is IOException

inline fun constraintsBuilder(func: Constraints.Builder.() -> Unit): Constraints {
    val constraints = Constraints.Builder()
    func.invoke(constraints)
    return constraints.build()
}

inline fun <reified T : Worker> periodicWorkRequestBuilder(
    repeatInterval: Long,
    repeatIntervalTimeUnit: TimeUnit,
    func: PeriodicWorkRequest.Builder.() -> Unit
): PeriodicWorkRequest {
    val builder = PeriodicWorkRequestBuilder<T>(repeatInterval, repeatIntervalTimeUnit)
    func.invoke(builder)
    return builder.build()
}
