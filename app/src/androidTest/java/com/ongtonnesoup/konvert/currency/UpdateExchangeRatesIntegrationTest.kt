package com.ongtonnesoup.konvert.currency

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.filters.LargeTest
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.BuildConfig
import com.ongtonnesoup.konvert.currency.data.domainToLocalMapper
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.currency.data.local.SQLiteExchangeRepository
import com.ongtonnesoup.konvert.currency.data.localToDomainMapper
import com.ongtonnesoup.konvert.currency.data.network.FixerIoClient
import com.ongtonnesoup.konvert.currency.data.network.FixerIoExchangeRepository
import com.ongtonnesoup.konvert.currency.data.networkToDomainMapper
import com.ongtonnesoup.konvert.default
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Test
import retrofit2.Retrofit

@LargeTest
class UpdateExchangeRatesIntegrationTest {

    @Test
    @UiThreadTest
    fun getExchangeRates() {
        // Given
        Timber.plant(timber.log.Timber.DebugTree())

        val okHttpClient = OkHttpClient.Builder().default()

        val retrofitClient = Retrofit.Builder()
                .default()
                .baseUrl("http://data.fixer.io/api/")
                .client(okHttpClient)
                .build()
                .create(FixerIoClient::class.java)
        val network = FixerIoExchangeRepository(retrofitClient, networkToDomainMapper(), FixerIoExchangeRepository.Configuration(BuildConfig.ACCESS_KEY))

        val database = Room.databaseBuilder(InstrumentationRegistry.getTargetContext(), AppDatabase::class.java, "test-db").allowMainThreadQueries().build()
        val local = SQLiteExchangeRepository(database.exchangeRatesDao(), domainToLocalMapper(), localToDomainMapper())

        val getLatestExchangeRates = GetLatestExchangeRates(network)
        val saveExchangeRates = SaveExchangeRates(local)
        Timber.d { "${Thread.currentThread()}" }
        val cut = UpdateExchangeRates(getLatestExchangeRates, saveExchangeRates)

        // When
        runBlocking { cut.getExchangeRates() }

        // Then
        // TODO assert something
    }

}