package com.ongtonnesoup.konvert.currency.domain

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.DataState
import com.rubylichtenstein.rxtest.assertions.should
import com.rubylichtenstein.rxtest.assertions.shouldEmit
import com.rubylichtenstein.rxtest.assertions.shouldHave
import com.rubylichtenstein.rxtest.extentions.test
import com.rubylichtenstein.rxtest.matchers.complete
import com.rubylichtenstein.rxtest.matchers.noErrors
import com.rubylichtenstein.rxtest.matchers.valueCount
import io.reactivex.Single
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class GetCurrentDataStateTest : Spek({

    given("load or schedule data") {
        val local = mock<ExchangeRepository>()
        val appState = mock<AppState>()
        val interactor = GetCurrentDataState(local, appState)

        on("load with no local data") {

            whenever(local.getExchangeRates()).thenReturn(Single.just(ExchangeRepository.NO_DATA))
            val observable = interactor.load()

            it("should return no data") {
                observable.test {
                    it.await()
                    it should complete()
                    it shouldHave noErrors()
                    it shouldHave valueCount(1)
                    it shouldEmit DataState.NO_DATA
                }
            }
        }

        on("load with local data") {

            whenever(local.getExchangeRates()).thenReturn(Single.just(ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("test", 1.1)))))
            val observable = interactor.load()

            it("should return schedule refresh") {
                observable.test {
                    it.await()
                    it should complete()
                    it shouldHave noErrors()
                    it shouldHave valueCount(1)
                    it shouldEmit DataState.CACHED_DATA
                }
            }
        }
    }
})