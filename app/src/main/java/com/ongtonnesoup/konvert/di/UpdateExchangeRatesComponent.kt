package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.currency.domain.UpdateExchangeRates
import com.ongtonnesoup.konvert.currency.domain.LoadOrScheduleExchangeRates
import com.ongtonnesoup.konvert.currency.job.UpdateExchangeRatesService
import com.ongtonnesoup.konvert.currency.work.UpdateExchangeRatesWorker
import dagger.Subcomponent

@Subcomponent(modules = arrayOf(InitialisationModule::class, JobModule::class))
interface UpdateExchangeRatesComponent {

    fun inject(target: UpdateExchangeRatesService)

    fun inject(target: UpdateExchangeRatesWorker)

    val loadOrSchedule: LoadOrScheduleExchangeRates

    val update: UpdateExchangeRates

}
