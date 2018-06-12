package com.ongtonnesoup.konvert.currency.di

import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import com.ongtonnesoup.konvert.currency.domain.LoadOrScheduleExchangeRates
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class TestInitialisationModule {

    @Provides
    fun provideLoadOrScheduleModule(@Named("local") repository: ExchangeRepository): LoadOrScheduleExchangeRates {
        return LoadOrScheduleExchangeRates(repository)
    }

}
