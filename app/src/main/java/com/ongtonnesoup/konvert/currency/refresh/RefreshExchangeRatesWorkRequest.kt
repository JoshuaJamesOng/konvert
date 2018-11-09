package com.ongtonnesoup.konvert.currency.refresh

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.UUID
import java.util.concurrent.TimeUnit

class RefreshExchangeRatesWorkRequest(private val workManager: WorkManager) {

    fun schedule(): UUID? {
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val update = PeriodicWorkRequest.Builder(
            RefreshExchangeRatesWorker::class.java,
            1, TimeUnit.DAYS
        ).setConstraints(constraint).build()

        workManager.enqueue(update)

        return update.id
    }
}
