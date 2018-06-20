package com.ongtonnesoup.konvert.currency.di

import com.ongtonnesoup.konvert.Schedulers
import com.ongtonnesoup.konvert.currency.data.domainToLocalMapper
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.currency.data.local.SQLiteExchangeRepository
import com.ongtonnesoup.konvert.currency.data.localToDomainMapper
import com.ongtonnesoup.konvert.currency.data.network.FixerIoClient
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import com.ongtonnesoup.konvert.currency.domain.UpdateExchangeRates
import com.ongtonnesoup.konvert.default
import dagger.Module
import dagger.Provides
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named

@Module
object TestJobModule {

    @Provides
    @JvmStatic
    fun provideRetrofit(okHttpClient: OkHttpClient): FixerIoClient {
        return Retrofit.Builder()
                .default()
                .baseUrl("https://api.fixer.io/")
                .client(okHttpClient)
                .build()
                .create(FixerIoClient::class.java)
    }

    @Provides
    @Named("network")
    @JvmStatic
    fun provideNetworkRepository(retrofitClient: FixerIoClient): ExchangeRepository {
        return object : ExchangeRepository {
            override fun getExchangeRates(): Single<ExchangeRepository.ExchangeRates> {
                val rate = ExchangeRepository.ExchangeRate("T$", 1.0)
                val exchangeRates = ExchangeRepository.ExchangeRates(listOf(rate))
                return Single.just(exchangeRates)
            }

            override fun putExchangeRates(rates: ExchangeRepository.ExchangeRates): Completable {
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

    @Provides
    @JvmStatic
    fun provideInteractor(@Named("network") network: ExchangeRepository,
                          @Named("local") local: ExchangeRepository,
                          schedulers: Schedulers): UpdateExchangeRates {
        return UpdateExchangeRates(network, local, schedulers)
    }

}
