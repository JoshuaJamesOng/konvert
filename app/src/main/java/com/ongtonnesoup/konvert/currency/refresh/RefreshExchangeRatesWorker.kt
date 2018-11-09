package com.ongtonnesoup.konvert.currency.refresh

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.di.Injector
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class RefreshExchangeRatesWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    @Inject
    lateinit var interactor: RefreshThenRescheduleExchangeRates

    override fun doWork(): Result {
        Timber.d { "Starting update exchange rates job" }
        inject()

        runBlocking {
            interactor.refreshThenReschedule()
        }

        // TODO Handle errors
        return Result.SUCCESS
    }

    private fun inject() {
        (applicationContext as Injector<RefreshExchangeRatesWorker>).inject(this)
    }
}
