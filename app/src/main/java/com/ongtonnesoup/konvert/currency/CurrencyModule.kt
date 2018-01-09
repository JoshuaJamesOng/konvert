package com.ongtonnesoup.konvert.currency

import com.ongtonnesoup.konvert.currency.local.SQLiteExchangeRepository
import com.ongtonnesoup.konvert.currency.network.FixerIoExchangeRepository
import dagger.Binds
import dagger.Module
import javax.inject.Named

@Module
abstract class CurrencyModule {

    @Binds
    @Named("network") abstract fun bindNetworkRepository(repository: FixerIoExchangeRepository) : ExchangeRepository

    @Binds
    @Named("local") abstract fun bindLocalRepository(repository: SQLiteExchangeRepository) : ExchangeRepository

}