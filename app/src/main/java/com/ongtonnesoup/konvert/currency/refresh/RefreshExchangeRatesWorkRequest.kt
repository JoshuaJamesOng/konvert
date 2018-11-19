package com.ongtonnesoup.konvert.currency.refresh

import androidx.work.NetworkType
import androidx.work.WorkManager
import com.ongtonnesoup.konvert.constraintsBuilder
import com.ongtonnesoup.konvert.periodicWorkRequestBuilder
import java.util.concurrent.TimeUnit
import java.util.UUID

class RefreshExchangeRatesWorkRequest(private val workManager: WorkManager) {

    fun schedule(): UUID? {
        val constraint = constraintsBuilder {
            setRequiredNetworkType(NetworkType.CONNECTED)
        }

        val update = periodicWorkRequestBuilder<RefreshExchangeRatesWorker>(1, TimeUnit.DAYS) {
            setConstraints(constraint)
        }

        workManager.enqueue(update)

        return update.id
    }
}
