package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.currency.data.domainToLocalMapper
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.currency.data.local.SQLiteExchangeRepository
import com.ongtonnesoup.konvert.currency.data.localToDomainMapper
import com.ongtonnesoup.konvert.currency.data.network.FixerIoClient
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import dagger.Module
import dagger.Provides
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Named

@Module
object TestDataSourcesModule {

    @Provides
    @Named("network")
    @JvmStatic
    fun provideNetworkRepository(): ExchangeRepository {
        return object : ExchangeRepository {
            override suspend fun getExchangeRates(): Single<ExchangeRepository.ExchangeRates> {
                val rate = ExchangeRepository.ExchangeRate("T$", 1.0)
                val exchangeRates = ExchangeRepository.ExchangeRates(listOf(rate))
                return Single.just(exchangeRates)
            }

            override suspend fun putExchangeRates(rates: ExchangeRepository.ExchangeRates): Completable {
                return Completable.error(UnsupportedOperationException())
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