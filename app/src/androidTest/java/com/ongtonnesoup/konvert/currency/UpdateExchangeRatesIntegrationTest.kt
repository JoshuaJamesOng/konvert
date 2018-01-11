package com.ongtonnesoup.konvert.currency

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.filters.LargeTest
import android.support.test.filters.MediumTest
import android.util.Log
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.currency.local.AppDatabase
import com.ongtonnesoup.konvert.currency.local.SQLiteExchangeRepository
import com.ongtonnesoup.konvert.currency.network.FixerIoClient
import com.ongtonnesoup.konvert.currency.network.FixerIoExchangeRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@LargeTest
class UpdateExchangeRatesIntegrationTest {

    @Before
    fun setUp() {
    }

    @Test
    fun getExchangeRates() {
        Timber.plant(timber.log.Timber.DebugTree())

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor())
                .build()
        val retrofitClient = Retrofit.Builder()
                .baseUrl("https://api.fixer.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .validateEagerly(true)
                .client(okHttpClient)
                .build()
                .create(FixerIoClient::class.java)
        val network = FixerIoExchangeRepository(retrofitClient, networkToDomainMapper())

        val database = Room.databaseBuilder(InstrumentationRegistry.getTargetContext(), AppDatabase::class.java, "test-db").allowMainThreadQueries().build()
        val local = SQLiteExchangeRepository(database.exchangeRatesDao(), domainToLocalMapper(), localToDomainMapper())

        val cut = UpdateExchangeRates(network, local)

        cut.getExchangeRates().subscribe(
                { Log.d("JJO", "Complete") },
                { Timber.e(it) }
        )
    }

}