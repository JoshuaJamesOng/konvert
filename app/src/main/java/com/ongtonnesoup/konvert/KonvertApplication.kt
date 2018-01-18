package com.ongtonnesoup.konvert

import android.app.Application
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.currency.UpdateExchangeRates
import com.ongtonnesoup.konvert.currency.domain.LoadOrScheduleExchangeRates
import com.ongtonnesoup.konvert.currency.domain.LoadOrScheduleExchangeRates.ExchangeRateStatus.NO_DATA
import com.ongtonnesoup.konvert.currency.domain.LoadOrScheduleExchangeRates.ExchangeRateStatus.SCHEDULE_REFRESH
import com.ongtonnesoup.konvert.currency.job.UpdateExchangeRatesJob
import com.ongtonnesoup.konvert.di.ApplicationComponent
import com.ongtonnesoup.konvert.di.ApplicationModule
import com.ongtonnesoup.konvert.di.DaggerApplicationComponent
import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Provider

class KonvertApplication : Application(), Provider<ApplicationComponent> {

    @Inject protected lateinit var loadOrScheduleExchangeRates: LoadOrScheduleExchangeRates

    private lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()

        loadOrScheduleExchangeRates()
    }

    override fun get(): ApplicationComponent = this.applicationComponent

    private fun loadOrScheduleExchangeRates() {
        loadOrScheduleExchangeRates.load()
                .flatMapCompletable { status ->
                    when (status) {
                        NO_DATA -> {
                            applicationComponent.getJobComponent().interactor.getExchangeRates()
                        }
                        SCHEDULE_REFRESH -> {
                            UpdateExchangeRatesJob.schedule(this)
                            Completable.complete()
                        }
                    }
                }
                .subscribe(
                        { Timber.d { "Loaded/scheduled exchange rates" } },
                        { Timber.e { "Could not determine whether to load or schedule" } }
                )
    }
}