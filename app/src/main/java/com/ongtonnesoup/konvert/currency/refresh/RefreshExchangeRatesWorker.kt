package com.ongtonnesoup.konvert.currency.refresh

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.di.Injector
import com.ongtonnesoup.konvert.state.RefreshState
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class RefreshExchangeRatesWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    @Inject
    lateinit var interactor: RefreshThenRescheduleExchangeRates

    override fun doWork(): Result {
        Timber.d { "Starting update exchange rates job" }
        inject()

        val status = runBlocking {
            interactor.refreshThenReschedule()
        }

        return if (status == RefreshState.SCHEDULED) {
            Result.SUCCESS
        } else {
            Result.FAILURE
        }
    }

    private fun inject() {
        (applicationContext as Injector<RefreshExchangeRatesWorker>).inject(this)
    }
}
