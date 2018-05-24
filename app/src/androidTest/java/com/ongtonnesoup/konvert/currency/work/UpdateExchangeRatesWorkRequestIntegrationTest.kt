package com.ongtonnesoup.konvert.currency.work

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.filters.LargeTest
import android.util.Log
import androidx.work.State
import androidx.work.WorkManager
import androidx.work.WorkStatus
import androidx.work.test.WorkManagerTestInitHelper
import junit.framework.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@LargeTest
class UpdateExchangeRatesWorkRequestIntegrationTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Test
    @UiThreadTest
    fun scheduleWork() {
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
    }

}