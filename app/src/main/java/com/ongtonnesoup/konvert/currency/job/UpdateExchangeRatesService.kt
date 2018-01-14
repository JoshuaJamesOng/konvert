package com.ongtonnesoup.konvert.currency.job

import android.app.job.JobParameters
import android.app.job.JobService
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.currency.UpdateExchangeRates
import com.ongtonnesoup.konvert.di.ApplicationComponent
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class UpdateExchangeRatesService : JobService() {

    @Inject protected lateinit var interactor: UpdateExchangeRates

    private val disposables = CompositeDisposable()

    init {
        (application as ApplicationComponent).getJobComponent().inject(this)
    }

    override fun onStartJob(params: JobParameters): Boolean {
        params.let {
            if (params.jobId == UpdateExchangeRatesJob.JOB_ID) {
                val disposable = interactor.getExchangeRates().subscribe(
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
        }
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        disposables.dispose()
        return false
    }

}