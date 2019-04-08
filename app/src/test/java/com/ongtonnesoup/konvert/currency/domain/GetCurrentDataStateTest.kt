package com.ongtonnesoup.konvert.currency.domain

import arrow.core.Try
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.DataState
import com.ongtonnesoup.konvert.state.State
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.called
import org.amshove.kluent.on
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.that
import org.amshove.kluent.Verify
import org.amshove.kluent.VerifyNoInteractions
import org.amshove.kluent.was
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class GetCurrentDataStateTest : Spek({

    given("no app state and no local data") {
        val appState = mock<AppState> {
            on { current() } doReturn State(dataState = DataState.UNKNOWN)
        }

        val local = mock<ExchangeRepository> {
            onBlocking { getExchangeRates() } doReturn Try.just(ExchangeRepository.ExchangeRates(emptyList()))
        }

        val cut = GetCurrentDataState(local, appState)

        on("load") {
            val result = runBlocking {
                cut.load()
            }

            it("checks local repository") {
                runBlocking { Verify on local that local.getExchangeRates() was called }
            }

            it("returns no data") {
                result shouldEqual DataState.NO_DATA
            }
        }
    }

    given("no app state and no cached local data") {
        val appState = mock<AppState> {
            on { current() } doReturn State(dataState = DataState.UNKNOWN)
        }

        val local = mock<ExchangeRepository> {
            onBlocking { getExchangeRates() } doReturn Try.just(ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("", 1.0))))
        }

        val cut = GetCurrentDataState(local, appState)

        on("load") {

            val result = runBlocking { cut.load() }

            it("checks local repository") {
                runBlocking { Verify on local that local.getExchangeRates() was called }
            }

            it("returns cached") {
                result shouldEqual DataState.CACHED_DATA
            }
        }
    }

    given("given cached app state") {
        val appState = mock<AppState> {
            on { current() } doReturn State(dataState = DataState.CACHED_DATA)
        }

        val local = mock<ExchangeRepository>()

        val cut = GetCurrentDataState(local, appState)

        on("load") {

            val result = runBlocking { cut.load() }

            it("doesn't check local repository") {
                VerifyNoInteractions on local
            }

            it("returns cached") {
                result shouldEqual DataState.CACHED_DATA
            }
        }
    }

    given("given no data app state") {
        val appState = mock<AppState> {
            on { current() } doReturn State(dataState = DataState.NO_DATA)
        }

        val local = mock<ExchangeRepository>()

        val cut = GetCurrentDataState(local, appState)

        on("load") {

            val result = runBlocking { cut.load() }

            it("doesn't check local repository") {
                VerifyNoInteractions on local
            }

            it("returns cached") {
                result shouldEqual DataState.NO_DATA
            }
        }
    }
})
