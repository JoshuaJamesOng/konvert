package com.ongtonnesoup.konvert.currency

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import com.rubylichtenstein.rxtest.assertions.should
import com.rubylichtenstein.rxtest.extentions.test
import com.rubylichtenstein.rxtest.matchers.complete
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class UpdateExchangeRatesTest : Spek({

    given("rates") {
        val exchangeRates = ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("network", 1.0)))

        val get = mock<GetLatestExchangeRates> {
            on { getExchangeRates() } doReturn Single.just(exchangeRates)
        }

        val sideEffect = TestObserver<Object>()
        val save = mock<SaveExchangeRates> {
            on { save(exchangeRates) } doReturn Completable.complete().doOnSubscribe(sideEffect::onSubscribe)
        }

        val cut = UpdateExchangeRates(get, save)

        on("getting exchange rates") {
            val observable = cut.getExchangeRates()

            it("should fetch and save") {
                observable.test {
                    it.await()
                    it should complete()
                }
                sideEffect.assertSubscribed()
            }
        }
    }

})