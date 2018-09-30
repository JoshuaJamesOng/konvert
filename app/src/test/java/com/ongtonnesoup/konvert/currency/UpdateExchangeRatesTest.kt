package com.ongtonnesoup.konvert.currency

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.amshove.kluent.Verify
import org.amshove.kluent.called
import org.amshove.kluent.on
import org.amshove.kluent.that
import org.amshove.kluent.was
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class UpdateExchangeRatesTest : Spek({

    given("latest exchange rates response") {
        val exchangeRates = ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("network", 1.0)))

        val get = mock<GetLatestExchangeRates> {
            on { getExchangeRates() } doReturn Single.just(exchangeRates)
        }

        given("local storage available") {
            val sideEffect = TestObserver<Any>()
            val save = mock<SaveExchangeRates> {
                on { save(exchangeRates) } doReturn Completable.complete().doOnSubscribe(sideEffect::onSubscribe)
            }

            val cut = UpdateExchangeRates(get, save)

            on("get") {
                val test = cut.getExchangeRates().test()

                it("should fetch") {
                    Verify on get that get.getExchangeRates() was called
                }

                it("should save") {
                    sideEffect.assertSubscribed()
                    Verify on save that save.save(exchangeRates) was called
                }

                it("should complete") {
                    test.assertComplete()
                    test.assertNoErrors()
                }
            }
        }
    }

})