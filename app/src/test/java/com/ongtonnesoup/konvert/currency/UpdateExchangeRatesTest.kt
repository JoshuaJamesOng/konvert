package com.ongtonnesoup.konvert.currency

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Completable
import io.reactivex.Single
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class UpdateExchangeRatesTest : Spek({

    given("successful network and local responses") {
        val network = mock<ExchangeRepository> {
            on { getExchangeRates() } doReturn Single.just(ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("network", 1.0))))
        }
        val local = mock<ExchangeRepository> {
            on { putExchangeRates(any()) } doReturn Completable.complete()
        }
        val cut = UpdateExchangeRates(network, local)

        on("getting exchange rates") {
            val observer = cut.getExchangeRates().test()

            it("should complete") {
                observer.assertComplete()
            }
        }
    }

})