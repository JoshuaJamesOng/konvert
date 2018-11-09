package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.currency.data.domainToLocalMapper
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.currency.data.local.SQLiteExchangeRepository
import com.ongtonnesoup.konvert.currency.data.localToDomainMapper
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object TestDataSourcesModule {

    @Provides
    @Named("network")
    @JvmStatic
    fun provideNetworkRepository(): ExchangeRepository {
        return object : ExchangeRepository {
            suspend override fun getExchangeRates(): ExchangeRepository.ExchangeRates {
                val rate = ExchangeRepository.ExchangeRate("T$", 1.0)
                return ExchangeRepository.ExchangeRates(listOf(rate))
            }

            suspend override fun putExchangeRates(rates: ExchangeRepository.ExchangeRates) {
                throw UnsupportedOperationException()
            }
        }
    }

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
}