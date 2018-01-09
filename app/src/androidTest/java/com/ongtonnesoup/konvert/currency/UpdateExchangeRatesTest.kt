package com.ongtonnesoup.konvert.currency

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import com.ongtonnesoup.konvert.currency.local.AppDatabase
import com.ongtonnesoup.konvert.currency.local.SQLiteExchangeRepository
import com.ongtonnesoup.konvert.currency.network.FixerIoClient
import com.ongtonnesoup.konvert.currency.network.FixerIoExchangeRepository
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class UpdateExchangeRatesTest {

    @Before
    fun setUp() {
    }

    @Test
    fun getExchangeRates() {
        val client = Retrofit.Builder()
                .baseUrl("http://www.fixer.io/")
                .build()
                .create(FixerIoClient::class.java)
        val network = FixerIoExchangeRepository(client, networkToLocalMapper())

        val database = Room.databaseBuilder(InstrumentationRegistry.getTargetContext(), AppDatabase::class.java, "test-db").allowMainThreadQueries().build()
        val local = SQLiteExchangeRepository(database.exchangeRatesDao(), domainToLocalMapper(), localToDomainMapper())

        val cut = UpdateExchangeRates(network, local)
    }

}