package com.ongtonnesoup.konvert.currency.di

import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import com.ongtonnesoup.konvert.currency.domain.GetCurrentDataState
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class TestInitialisationModule {

    @Provides
    fun provideLoadOrScheduleModule(@Named("local") repository: ExchangeRepository): GetCurrentDataState {
        return GetCurrentDataState(repository)
    }

}
