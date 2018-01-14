package com.ongtonnesoup.konvert.currency.job

import android.app.job.JobInfo
import android.app.job.JobInfo.NETWORK_TYPE_ANY
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import java.util.concurrent.TimeUnit

class UpdateExchangeRatesJob {

    companion object {
        const val JOB_ID = 1515888771

        fun schedule(context: Context) {
            val jobService = ComponentName(context, UpdateExchangeRatesService::class.java)

            val jobInfo = JobInfo.Builder(JOB_ID, jobService).apply {
                setRequiredNetworkType(NETWORK_TYPE_ANY)
                setRequiresDeviceIdle(true)
                setPersisted(true)

                val interval = TimeUnit.DAYS.toMillis(1)
                if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) setMinimumLatency(interval) else setPeriodic(interval)
            }.build()

            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(jobInfo)
        }
    }

}