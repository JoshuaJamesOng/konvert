package com.ongtonnesoup.konvert.currency.domain

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.ongtonnesoup.konvert.currency.ExchangeRepository
import io.reactivex.Single
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class LoadOrScheduleExchangeRatesTest : Spek({

    given("load or schedule data") {
        val local = mock<ExchangeRepository>()
        val interactor = LoadOrScheduleExchangeRates(local)

        on("load with no local data") {

            whenever(local.getExchangeRates()).thenReturn(Single.just(ExchangeRepository.NO_DATA))
            val observable = interactor.load()

            it("should return no data") {
                val observer = observable.test().await()
                observer.assertComplete()
                observer.assertValue(LoadOrScheduleExchangeRates.ExchangeRateStatus.NO_DATA)
            }
        }

        on("load with local data") {

            whenever(local.getExchangeRates()).thenReturn(Single.just(ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("test", 1.1)))))
            val observable = interactor.load()

            it("should return schedule refresh") {
                val observer = observable.test().await()
                observer.assertComplete()
                observer.assertValue(LoadOrScheduleExchangeRates.ExchangeRateStatus.SCHEDULE_REFRESH)
            }
        }
    }
})