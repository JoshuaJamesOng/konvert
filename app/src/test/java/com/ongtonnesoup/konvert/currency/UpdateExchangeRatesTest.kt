package com.ongtonnesoup.konvert.currency

import arrow.core.Try
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.Verify
import org.amshove.kluent.VerifyNotCalled
import org.amshove.kluent.called
import org.amshove.kluent.on
import org.amshove.kluent.that
import org.amshove.kluent.was
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object UpdateExchangeRatesTest : Spek({

    Feature("Updating rates") {
        val get by memoized { mock<GetLatestExchangeRates>() }
        val save by memoized { mock<SaveExchangeRates>() }

        val exchangeRates = ExchangeRepository.ExchangeRates(
            listOf(
                ExchangeRepository.ExchangeRate(
                    "network",
                    1.0
                )
            )
        )

        Scenario("Rates updated") {
            Given("Exchange rates available") {
                get.stub {
                    onBlocking { getExchangeRates() } doReturn Try.just(exchangeRates) // TODO Confirm why we switched methods
                }
            }

            When("Updating exchange rates") {
                val cut = UpdateExchangeRates(get, save)
                runBlocking { cut.getExchangeRates() }
            }

            Then("Exchange rates fetched") {
                runBlocking { Verify on get that get.getExchangeRates() was called }
            }

            Then("Exchange rates saved") {
                runBlocking { Verify on save that save.save(exchangeRates) was called }
            }
        }

        Scenario("Rates not updated") {
            Given("Exchange rates not available") {
                get.stub {
                    onBlocking { getExchangeRates() } doReturn Try.raiseError(ExchangeRepository.NoDataException())
                }
            }

            When("Updating exchange rates") {
                val cut = UpdateExchangeRates(get, save)
                runBlocking { cut.getExchangeRates() }
            }

            Then("Exchange rates fetched") {
                runBlocking { Verify on get that get.getExchangeRates() was called }
            }

            Then("Nothing to save") {
                runBlocking { VerifyNotCalled on save that save.save(exchangeRates) }
            }
        }
    }
})
