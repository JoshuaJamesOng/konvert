package com.ongtonnesoup.konvert.currency

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.called
import org.amshove.kluent.on
import org.amshove.kluent.that
import org.amshove.kluent.Verify
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
            onBlocking { getNetworkExchangeRates() } doReturn exchangeRates // TODO Confirm why we switched methods
        }

        val save = mock<SaveExchangeRates>()

        val cut = UpdateExchangeRates(get, save)

        on("get") {
            runBlocking {
                cut.getExchangeRates()
            }

            it("should fetch") {
                runBlocking { Verify on get that get.getNetworkExchangeRates() was called }
            }

            it("should save") {
                runBlocking { Verify on save that save.save(exchangeRates) was called }
            }
        }
    }
})
