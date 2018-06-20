package com.ongtonnesoup.konvert

import io.reactivex.Scheduler

interface Schedulers {

    fun getWorkerScheduler(): Scheduler

    fun getPostExecutionScheduler(): Scheduler

}