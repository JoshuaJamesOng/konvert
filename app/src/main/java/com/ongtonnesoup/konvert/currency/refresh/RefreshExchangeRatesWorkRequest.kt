package com.ongtonnesoup.konvert.currency.refresh

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.ongtonnesoup.konvert.constraintsBuilder
import com.ongtonnesoup.konvert.periodicWorkRequestBuilder
import java.util.concurrent.TimeUnit
import java.util.UUID

class RefreshExchangeRatesWorkRequest(private val workManager: WorkManager) {

    fun schedule(): UUID? {
        val name = RefreshExchangeRatesWorker::class.java.name
        val existingWork = getExistingUniqueWork(name)
        return existingWork?.id ?: enqueueNewUniqueWork(name)
    }

    private fun getExistingUniqueWork(name: String): WorkInfo? {
        val existingWorkSource = workManager.getWorkInfosForUniqueWork(name)
        // TODO Check if this ListenableFuture stuff is safe for interruptions
        val existingWork = existingWorkSource.get()
        return when {
            existingWork.size == 0 -> null
            1 < existingWork.size -> throw IllegalStateException("Unique work $name queued more than once")
            else -> existingWork[0]
        }
    }

    private fun enqueueNewUniqueWork(name: String): UUID {
        val constraint = constraintsBuilder {
            setRequiredNetworkType(NetworkType.CONNECTED)
        }

        val update = periodicWorkRequestBuilder<RefreshExchangeRatesWorker>(1, TimeUnit.DAYS) {
            setConstraints(constraint)
        }

        workManager.enqueueUniquePeriodicWork(
            name,
            ExistingPeriodicWorkPolicy.KEEP,
            update
        )

        return update.id
    }
}
