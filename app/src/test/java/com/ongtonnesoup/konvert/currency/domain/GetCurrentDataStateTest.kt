package com.ongtonnesoup.konvert.currency.domain

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.DataState
import com.ongtonnesoup.konvert.state.State
import io.reactivex.Observable
import io.reactivex.Single
import org.amshove.kluent.Verify
import org.amshove.kluent.VerifyNoInteractions
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
class GetCurrentDataStateTest : Spek({

    given("no data") {
        val appState = mock<AppState> {
            on { updates() } doReturn Observable.just(State(dataState = DataState.UNKNOWN))
        }

        val local = mock<ExchangeRepository> {
            on { getExchangeRates() } doReturn Single.just(ExchangeRepository.ExchangeRates(emptyList()))
        }

        val cut = GetCurrentDataState(local, appState)

        on("load") {

            val test = cut.load().test()

            it ("checks local repository") {
                Verify on local that local.getExchangeRates() was called
            }

            it ("returns no data") {
                test.assertResult(DataState.NO_DATA)
            }
        }
    }

    given("cached local data") {
        val appState = mock<AppState> {
            on { updates() } doReturn Observable.just(State(dataState = DataState.UNKNOWN))
        }

        val local = mock<ExchangeRepository> {
            on { getExchangeRates() } doReturn Single.just(ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("", 1.0))))
        }

        val cut = GetCurrentDataState(local, appState)

        on("load") {

            val test = cut.load().test()

            it ("checks local repository") {
                Verify on local that local.getExchangeRates() was called
            }

            it ("returns cached") {
                test.assertResult(DataState.CACHED_DATA)
            }
        }
    }

    given("app state") {
        val appState = mock<AppState> {
            on { updates() } doReturn Observable.just(State(dataState = DataState.NO_DATA), State(dataState = DataState.CACHED_DATA))
        }

        val local = mock<ExchangeRepository> {
            on { getExchangeRates() } doReturn Single.error(UnsupportedOperationException())
        }

        val cut = GetCurrentDataState(local, appState)

        on ("load") {

            val test = cut.load().test()

            it ("doesn't check local repository") {
                VerifyNoInteractions on local
            }

            it ("returns cached") {
                test.assertResult(DataState.NO_DATA)
            }
        }
    }

})