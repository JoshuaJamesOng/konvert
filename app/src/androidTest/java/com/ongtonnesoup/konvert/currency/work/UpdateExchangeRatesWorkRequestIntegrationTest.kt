package com.ongtonnesoup.konvert.currency.work

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.filters.LargeTest
import androidx.work.State
import androidx.work.WorkManager
import androidx.work.test.WorkManagerTestInitHelper
import com.ongtonnesoup.konvert.currency.TestApplication
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.currency.di.TestUpdateExchangeRatesComponent
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@LargeTest
class UpdateExchangeRatesWorkRequestIntegrationTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var component: TestUpdateExchangeRatesComponent

    private val appDatabase: AppDatabase by lazy {
        component.appDatabase()
    }

    @Before
    fun setUp() {
        val application = InstrumentationRegistry.getTargetContext().applicationContext as TestApplication
        component = application.updateExchangeRatesComponent
    }

    @Test
    @UiThreadTest
    fun scheduleWork() {
        assertTrue(appDatabase.exchangeRatesDao().getAll().blockingIterable().count() < 1)
        WorkManagerTestInitHelper.initializeTestWorkManager(InstrumentationRegistry.getTargetContext())

        val workRequest = UpdateExchangeRatesWorkRequest(WorkManager.getInstance())
        val uuid = workRequest.schedule()

        var workRan = false
        val data = WorkManager.getInstance().getStatusById(uuid!!)
        data.observeForever {
            if (State.RUNNING == it!!.state) workRan = true
        }

        WorkManagerTestInitHelper.getTestDriver().setAllConstraintsMet(uuid)

        assertTrue(workRan)
        assertTrue(0 < appDatabase.exchangeRatesDao().getAll().blockingIterable().count())
    }

}