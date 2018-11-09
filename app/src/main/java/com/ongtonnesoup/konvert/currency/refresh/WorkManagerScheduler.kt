package com.ongtonnesoup.konvert.currency.refresh

import androidx.work.WorkManager
import io.reactivex.Completable
import javax.inject.Inject

class WorkManagerScheduler @Inject constructor(private val workManager: WorkManager) : Scheduler {

    override fun schedule(): Completable {
        return Completable.fromCallable {
            val request = RefreshExchangeRatesWorkRequest(workManager)
            request.schedule()
        }
    }
}
