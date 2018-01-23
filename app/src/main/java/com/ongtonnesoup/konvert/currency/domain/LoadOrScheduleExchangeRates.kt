package com.ongtonnesoup.konvert.currency.domain

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Named

class LoadOrScheduleExchangeRates(@Named("local") private val local: ExchangeRepository) {

    fun load(): Single<ExchangeRateStatus> {
        return local.getExchangeRates()
                .subscribeOn(Schedulers.io())
                .flatMap { rates ->
                    if (rates.rates.isEmpty()) {
                        Single.just(ExchangeRateStatus.NO_DATA)
                    } else {
                        Single.just(ExchangeRateStatus.SCHEDULE_REFRESH)
                    }
                }
    }

    enum class ExchangeRateStatus {
        NO_DATA,
        SCHEDULE_REFRESH
    }

}
