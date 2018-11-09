package com.ongtonnesoup.konvert

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers

class InteractorSchedulers : Schedulers {

    override fun getWorkerScheduler(): Scheduler = io.reactivex.schedulers.Schedulers.io()

    override fun getPostExecutionScheduler(): Scheduler = AndroidSchedulers.mainThread()
}
