package com.ongtonnesoup.konvert.currency.refresh

import javax.inject.Inject

class EnableBackgroundSync @Inject constructor(private val scheduler: Scheduler) {
    fun enableBackgroundSync() {
        scheduler.schedule()
    }
}