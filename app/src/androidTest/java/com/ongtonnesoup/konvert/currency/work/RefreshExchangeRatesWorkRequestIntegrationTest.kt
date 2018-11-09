package com.ongtonnesoup.konvert.currency.work

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.InstrumentationRegistry
import androidx.test.annotation.UiThreadTest
import androidx.test.filters.LargeTest
import androidx.work.State
import androidx.work.WorkManager
import androidx.work.test.WorkManagerTestInitHelper
import com.ongtonnesoup.konvert.TestApplication
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.currency.di.TestWorkerComponent
import com.ongtonnesoup.konvert.currency.refresh.RefreshExchangeRatesWorkRequest
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@LargeTest
class RefreshExchangeRatesWorkRequestIntegrationTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var component: TestWorkerComponent

    private val appDatabase: AppDatabase by lazy {
        component.appDatabase()
    }

    @Before
    fun setUp() {
        val application = InstrumentationRegistry.getTargetContext().applicationContext as TestApplication
        component = application.workerComponent

        assertTrue("No data", appDatabase.exchangeRatesDao().getAll().count() < 1)
    }

    @Test
    @UiThreadTest
    fun scheduleWork() {
        WorkManagerTestInitHelper.initializeTestWorkManager(InstrumentationRegistry.getTargetContext())

        val workRequest = RefreshExchangeRatesWorkRequest(WorkManager.getInstance())
        val uuid = workRequest.schedule()

        val states = mutableListOf<State>()
        val data = WorkManager.getInstance().getStatusByIdLiveData(uuid!!)
        data.observeForever {
            states.add(it!!.state)
        }

        WorkManagerTestInitHelper.getTestDriver().setAllConstraintsMet(uuid)

        assertTrue("Network data is cached locally", 0 < appDatabase.exchangeRatesDao().getAll().count())
        assertEquals("Work is re-enqueued", listOf(State.ENQUEUED, State.RUNNING, State.ENQUEUED), states)
    }

}