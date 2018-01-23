package com.ongtonnesoup.konvert.currency.job

import android.app.job.JobInfo
import android.app.job.JobInfo.NETWORK_TYPE_ANY
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.github.ajalt.timberkt.Timber
import java.util.concurrent.TimeUnit

class UpdateExchangeRatesJob {

    companion object {
        const val JOB_ID = 1515888771

        fun schedule(context: Context) {
            val jobService = ComponentName(context, UpdateExchangeRatesService::class.java)

            val jobInfo = JobInfo.Builder(JOB_ID, jobService).apply {
                setRequiredNetworkType(NETWORK_TYPE_ANY)
                setPersisted(true)
                setPeriodic(TimeUnit.MINUTES.toMillis(1))
            }.build()

            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

            val pendingJob = jobScheduler.getPendingJob(JOB_ID)

            if (pendingJob == null) {
                val scheduled = jobScheduler.schedule(jobInfo)

                if (scheduled != JobScheduler.RESULT_SUCCESS) {
                    Timber.e { "Error while scheduling job" }
                }
            } else {
                Timber.d { "Job already scheduled. Not re-scheduling so timer does not reset" }
            }
        }
    }

}