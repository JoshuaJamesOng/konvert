package com.ongtonnesoup.konvert.currency.refresh

import androidx.work.Worker
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.di.Injector
import javax.inject.Inject

class RefreshExchangeRatesWorker : Worker() {

    @Inject
    lateinit var interactor: RefreshThenRescheduleExchangeRates

    override fun doWork(): Worker.Result {
        Timber.d { "Starting update exchange rates job" }
        inject()

        val throwable = interactor.refreshThenReschedule().blockingGet()

        return if (throwable == null) {
            Worker.Result.SUCCESS
        } else {
            Timber.e(throwable)
            Worker.Result.FAILURE
        }
    }

    private fun inject() {
        (applicationContext as Injector<RefreshExchangeRatesWorker>).inject(this)
    }

}