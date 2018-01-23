package com.ongtonnesoup.konvert.di

import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import com.ongtonnesoup.konvert.currency.data.domainToLocalMapper
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.currency.data.local.SQLiteExchangeRepository
import com.ongtonnesoup.konvert.currency.data.localToDomainMapper
import com.ongtonnesoup.konvert.currency.data.network.FixerIoClient
import com.ongtonnesoup.konvert.currency.data.network.FixerIoExchangeRepository
import com.ongtonnesoup.konvert.currency.data.networkToDomainMapper
import com.ongtonnesoup.konvert.currency.domain.UpdateExchangeRates
import com.ongtonnesoup.konvert.default
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named

@Module
object JobModule {

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
        return FixerIoExchangeRepository(
                retrofitClient,
                networkToDomainMapper()
        )
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
                          @Named("local") local: ExchangeRepository): UpdateExchangeRates {
        return UpdateExchangeRates(network, local)
    }

}
