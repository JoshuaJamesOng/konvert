package com.ongtonnesoup.konvert.currency.work

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.annotation.UiThreadTest
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.ongtonnesoup.konvert.TestApplication
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.currency.di.TestWorkerComponent
import com.ongtonnesoup.konvert.currency.refresh.RefreshExchangeRatesWorkRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
        val application =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApplication
        component = application.workerComponent

        assertTrue("No data", appDatabase.exchangeRatesDao().getAll().count() < 1)
    }

    @Test
    @UiThreadTest
    fun scheduleWork() {
        WorkManagerTestInitHelper.initializeTestWorkManager(InstrumentationRegistry.getInstrumentation().targetContext)

        val workRequest = RefreshExchangeRatesWorkRequest(WorkManager.getInstance())
        val uuid = workRequest.schedule()

        val states = mutableListOf<WorkInfo.State>()
        val data = WorkManager.getInstance().getWorkInfoByIdLiveData(uuid!!)
        data.observeForever {
            states.add(it!!.state)
        }

        WorkManagerTestInitHelper.getTestDriver().setAllConstraintsMet(uuid)

        assertTrue(
            "Network data is cached locally",
            0 < appDatabase.exchangeRatesDao().getAll().count()
        )
        assertEquals(
            "Work is re-enqueued",
            listOf(WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED),
            states
        )
    }
}
