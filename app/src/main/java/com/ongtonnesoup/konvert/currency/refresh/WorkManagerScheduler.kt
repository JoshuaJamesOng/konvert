package com.ongtonnesoup.konvert.currency.refresh

import androidx.work.WorkManager
import javax.inject.Inject

class WorkManagerScheduler @Inject constructor(
        private val workManager: WorkManager
) : Scheduler {
    override fun schedule() {
        val request = RefreshExchangeRatesWorkRequest(workManager)
        request.schedule()
    }
}
