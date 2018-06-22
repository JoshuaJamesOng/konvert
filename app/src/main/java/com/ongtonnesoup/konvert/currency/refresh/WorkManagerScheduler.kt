package com.ongtonnesoup.konvert.currency.refresh

import androidx.work.WorkManager
import com.ongtonnesoup.konvert.currency.work.UpdateExchangeRatesWorkRequest
import io.reactivex.Completable
import javax.inject.Inject

class WorkManagerScheduler @Inject constructor(private val workManager: WorkManager) : Scheduler {

    override fun schedule(): Completable {
        return Completable.fromCallable {
            val request = UpdateExchangeRatesWorkRequest(workManager)
            request.schedule()
        }
    }

}