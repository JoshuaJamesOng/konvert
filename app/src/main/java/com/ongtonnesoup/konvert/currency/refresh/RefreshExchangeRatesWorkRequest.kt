package com.ongtonnesoup.konvert.currency.refresh

import androidx.work.*
import java.util.UUID
import java.util.concurrent.TimeUnit

class RefreshExchangeRatesWorkRequest(private val workManager: WorkManager) {

    fun schedule(): UUID? {
        val constraint = ConstraintsBuilder {
            setRequiredNetworkType(NetworkType.CONNECTED)
        }

        val update = PeriodicWorkRequest<RefreshExchangeRatesWorker>(1, TimeUnit.DAYS) {
            setConstraints(constraint)
        }

        workManager.enqueue(update)

        return update.id
    }

    private inline fun ConstraintsBuilder(func: Constraints.Builder.() -> Unit): Constraints {
        val constraints = Constraints.Builder()
        func.invoke(constraints)
        return constraints.build()
    }

    private inline fun <reified T : Worker> PeriodicWorkRequest(
            repeatInterval: Long,
            repeatIntervalTimeUnit: TimeUnit,
            func: PeriodicWorkRequest.Builder.() -> Unit): PeriodicWorkRequest {
        val builder = PeriodicWorkRequestBuilder<T>(repeatInterval, repeatIntervalTimeUnit)
        func.invoke(builder)
        return builder.build()
    }
}
