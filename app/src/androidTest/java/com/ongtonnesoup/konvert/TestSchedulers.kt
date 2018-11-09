package com.ongtonnesoup.konvert

import io.reactivex.Scheduler

class TestSchedulers : Schedulers {

    override fun getWorkerScheduler(): Scheduler = io.reactivex.schedulers.Schedulers.trampoline()

    override fun getPostExecutionScheduler(): Scheduler = io.reactivex.schedulers.Schedulers.trampoline()
}
