package com.ongtonnesoup.konvert.currency.refresh

import androidx.work.*
import java.util.UUID
import java.util.concurrent.TimeUnit

class RefreshExchangeRatesWorkRequest(private val workManager: WorkManager) {

    fun schedule(): UUID? {
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val update = PeriodicWorkRequestBuilder<RefreshExchangeRatesWorker>(
            1, TimeUnit.DAYS
        ).setConstraints(constraint).build()

        workManager.enqueue(update)

        return update.id
    }
}
