package com.ongtonnesoup.konvert.currency.refresh

import javax.inject.Inject

class ScheduleRefresh @Inject constructor(private val scheduler: Scheduler) {

    fun scheduleRefresh() = scheduler.schedule()

}