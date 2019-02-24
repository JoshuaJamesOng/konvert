package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.BuildConfig
import com.ongtonnesoup.konvert.currency.data.domainToLocalMapper
import com.ongtonnesoup.konvert.currency.data.fixed.FixedExchangeRepository
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.currency.data.local.SQLiteExchangeRepository
import com.ongtonnesoup.konvert.currency.data.localToDomainMapper
import com.ongtonnesoup.konvert.currency.data.network.FixerIoClient
import com.ongtonnesoup.konvert.currency.data.network.FixerIoExchangeRepository
import com.ongtonnesoup.konvert.currency.data.networkToDomainMapper
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object DataSourcesModule {

    @Provides
    @Named("local")
    @JvmStatic
    fun provideLocalRepository(database: AppDatabase): ExchangeRepository {
        return SQLiteExchangeRepository(
                database.exchangeRatesDao(),
                domainToLocalMapper(),
                localToDomainMapper()
        )
    }

    @Provides
    @Named("network")
    @JvmStatic
    fun provideNetworkRepository(retrofitClient: FixerIoClient): ExchangeRepository {
        return if (BuildConfig.USE_FIXED_EXCHANGE_RATES) {
            FixedExchangeRepository()
        } else {
            FixerIoExchangeRepository(
                    retrofitClient,
                    networkToDomainMapper()
            )
        }
    }
}
