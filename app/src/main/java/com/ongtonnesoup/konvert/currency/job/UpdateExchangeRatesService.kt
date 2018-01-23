package com.ongtonnesoup.konvert.currency.job

import android.app.job.JobParameters
import android.app.job.JobService
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.currency.domain.UpdateExchangeRates
import com.ongtonnesoup.konvert.di.ApplicationComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Provider

class UpdateExchangeRatesService : JobService() {

    @Inject protected lateinit var interactor: UpdateExchangeRates

    private val disposables = CompositeDisposable()

    override fun onStartJob(params: JobParameters): Boolean {
        Timber.d { "Starting update exchange rates job" }
        inject()

        if (params.jobId == UpdateExchangeRatesJob.JOB_ID) {
            val disposable = interactor.getExchangeRates()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                Timber.d { "Updated exchange rates" }
                                jobFinished(params, false)
                            },
                            { error ->
                                Timber.e(error)
                                jobFinished(params, false)
                            }
                    )

            disposables.add(disposable)
        }
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        Timber.d { "Stopping update exchange rates job" }
        disposables.dispose()
        return true
    }

    private fun inject() {
        val applicationComponent: Any = (application as Provider<*>).get()
        if (applicationComponent is ApplicationComponent) {
            applicationComponent.getUpdateExchangeRatesComponent().inject(this)
        }
    }
}
