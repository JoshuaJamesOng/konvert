package com.ongtonnesoup.konvert.currency.refresh

import io.reactivex.Completable

interface Scheduler {

    fun schedule(): Completable
}
