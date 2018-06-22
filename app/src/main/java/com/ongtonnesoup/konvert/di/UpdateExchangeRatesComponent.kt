package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.currency.refresh.RefreshExchangeRatesWorker
import dagger.Subcomponent

@Subcomponent
interface UpdateExchangeRatesComponent {

    fun inject(target: RefreshExchangeRatesWorker)

}
