package com.ongtonnesoup.konvert.currency.work

import androidx.work.Worker
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.currency.domain.UpdateExchangeRates
import com.ongtonnesoup.konvert.di.ApplicationComponent
import javax.inject.Inject
import javax.inject.Provider

class UpdateExchangeRatesWorker : Worker() {

    @Inject
    protected lateinit var interactor: UpdateExchangeRates

    override fun doWork(): WorkerResult {
        Timber.d { "Starting update exchange rates job" }
        inject()

        val throwable = interactor.getExchangeRates().blockingGet()

        return if (throwable == null) {
            WorkerResult.SUCCESS
        } else {
            WorkerResult.FAILURE
        }
    }

    private fun inject() {
        val applicationComponent: Any = (applicationContext as Provider<*>).get()
        if (applicationComponent is ApplicationComponent) {
            applicationComponent.getUpdateExchangeRatesComponent().inject(this)
        }
    }

}