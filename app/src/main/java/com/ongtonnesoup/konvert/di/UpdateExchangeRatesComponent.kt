package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.currency.job.UpdateExchangeRatesService
import com.ongtonnesoup.konvert.currency.work.UpdateExchangeRatesWorker
import dagger.Subcomponent

@Subcomponent
interface UpdateExchangeRatesComponent {

    fun inject(target: UpdateExchangeRatesService)

    fun inject(target: UpdateExchangeRatesWorker)

}
