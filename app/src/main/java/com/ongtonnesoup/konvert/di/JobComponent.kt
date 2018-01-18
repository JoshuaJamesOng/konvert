package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.currency.UpdateExchangeRates
import com.ongtonnesoup.konvert.currency.job.UpdateExchangeRatesService
import dagger.Subcomponent

@Subcomponent(modules = arrayOf(JobModule::class))
interface JobComponent {

    fun inject(target: UpdateExchangeRatesService)

    val interactor: UpdateExchangeRates

}