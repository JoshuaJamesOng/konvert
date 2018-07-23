package com.ongtonnesoup.konvert.currency

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.filters.LargeTest
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.InteractorSchedulers
import com.ongtonnesoup.konvert.currency.data.domainToLocalMapper
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.currency.data.local.SQLiteExchangeRepository
import com.ongtonnesoup.konvert.currency.data.localToDomainMapper
import com.ongtonnesoup.konvert.currency.data.network.FixerIoClient
import com.ongtonnesoup.konvert.currency.data.network.FixerIoExchangeRepository
import com.ongtonnesoup.konvert.currency.data.networkToDomainMapper
import com.ongtonnesoup.konvert.default
import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.State
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
                .baseUrl("https://api.fixer.io/")
                .client(okHttpClient)
                .build()
                .create(FixerIoClient::class.java)
        val network = FixerIoExchangeRepository(retrofitClient, networkToDomainMapper())

        val database = Room.databaseBuilder(InstrumentationRegistry.getTargetContext(), AppDatabase::class.java, "test-db").allowMainThreadQueries().build()
        val local = SQLiteExchangeRepository(database.exchangeRatesDao(), domainToLocalMapper(), localToDomainMapper())

        val appState = AppState(State())
        val getLatestExchangeRates = GetLatestExchangeRates(network, appState)
        val saveExchangeRates = SaveExchangeRates(local, appState)
        Timber.d { "${Thread.currentThread()}" }
        val cut = UpdateExchangeRates(getLatestExchangeRates, saveExchangeRates)

        // When
        val observable = cut.getExchangeRates().test()

        // Then
        observable
                .awaitTerminalEvent()

        observable
                .assertComplete()
    }

}