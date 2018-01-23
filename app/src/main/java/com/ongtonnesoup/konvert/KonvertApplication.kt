package com.ongtonnesoup.konvert

import android.app.Application
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.currency.domain.LoadOrScheduleExchangeRates.ExchangeRateStatus.NO_DATA
import com.ongtonnesoup.konvert.currency.domain.LoadOrScheduleExchangeRates.ExchangeRateStatus.SCHEDULE_REFRESH
import com.ongtonnesoup.konvert.currency.job.UpdateExchangeRatesJob
import com.ongtonnesoup.konvert.di.ApplicationComponent
import com.ongtonnesoup.konvert.di.ApplicationModule
import com.ongtonnesoup.konvert.di.DaggerApplicationComponent
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Provider

class KonvertApplication : Application(), Provider<ApplicationComponent> {

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
        val updateExchangeRatesComponent = applicationComponent.getUpdateExchangeRatesComponent()
        val loadOrScheduleExchangeRates = updateExchangeRatesComponent.loadOrSchedule

        val schedule = {
            UpdateExchangeRatesJob.schedule(this)
            Completable.complete()
        }

        loadOrScheduleExchangeRates.load()
                .flatMapCompletable { status ->
                    when (status) {
                        NO_DATA -> {
                            Timber.d { "No data. Fetching exchange rates" }
                            updateExchangeRatesComponent.update.getExchangeRates()
                                    .andThen {
                                        Timber.d { "Scheduling job after force fetch" }
                                        schedule()
                                    }
                        }
                        SCHEDULE_REFRESH -> {
                            Timber.d { "Existing data. Scheduling Job" }
                            schedule()
                        }
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { Timber.d { "Loaded/scheduled exchange rates" } },
                        { Timber.e { "Could not determine whether to load or schedule" } }
                )
    }
}