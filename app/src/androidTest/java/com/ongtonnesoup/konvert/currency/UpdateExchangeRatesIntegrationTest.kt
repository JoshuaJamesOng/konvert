package com.ongtonnesoup.konvert.currency

import androidx.test.annotation.UiThreadTest
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.ongtonnesoup.konvert.TestApplication
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.currency.di.TestApplicationComponent
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@LargeTest
class UpdateExchangeRatesIntegrationTest {

    private lateinit var component: TestApplicationComponent

    private val updateExchangeRates: UpdateExchangeRates by lazy {
        component.updateExchangeRates()
    }

    private val appDatabase: AppDatabase by lazy {
        component.appDatabase()
    }

    @Before
    fun setUp() {
        val application = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApplication
        component = application.appComponent

        assertTrue("No data", appDatabase.exchangeRatesDao().getAll().count() < 1)
    }

    @Test
    @UiThreadTest
    fun getExchangeRates() {
        // Given When
        runBlocking { updateExchangeRates.getExchangeRates() }

        // Then
        assertTrue("Network data is cached locally", 0 < appDatabase.exchangeRatesDao().getAll().count())
    }
}
