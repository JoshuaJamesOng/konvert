package com.ongtonnesoup.konvert.currency.data.network

import arrow.core.Try
import com.nhaarman.mockitokotlin2.*
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldHaveTheSameClassAs
import org.junit.Assert.fail
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import java.io.IOException
import kotlin.test.assertFailsWith

private const val BASE_CURRENCY = "GBP"

object FixerIoExchangeRepositoryTest : Spek({
    lateinit var cut: FixerIoExchangeRepository

    Feature("FixerIOExchangeRepository") {

        val client by memoized { mock<FixerIoClient>() }
        val mapper by memoized { mock<(FixerIoClient.Response) -> ExchangeRepository.ExchangeRates>() }
        val networkResponse = FixerIoClient.Response("base", "date", emptyMap())
        val networkError = CompletableDeferred<FixerIoClient.Response>().apply {
            completeExceptionally(IOException())
        }
        val mappedResponse = ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("test", 1.0)))

        Scenario("Successful network call") {

            Given("Rates available") {
                client.stub {
                    on { getLatest(BASE_CURRENCY) } doReturn CompletableDeferred(networkResponse)
                }
                mapper.stub {
                    on { invoke(networkResponse) } doReturn mappedResponse
                }
            }

            lateinit var result: Try<ExchangeRepository.ExchangeRates>
            When("Exchange rates fetched") {
                cut = FixerIoExchangeRepository(client, mapper)
                result = runBlocking { cut.getExchangeRates() }
            }

            Then("API call made") {
                verify(client).getLatest(BASE_CURRENCY)
            }

            Then("API response is mapped") {
                argumentCaptor<FixerIoClient.Response>().apply {
                    verify(mapper).invoke(capture())

                    firstValue shouldEqual networkResponse
                }
            }

            Then("Mapped response is returned") {
                result shouldEqual Try.just(mappedResponse)
            }
        }

        Scenario("Unsuccessful network call") {

            Given("Network error") {
                client.stub {
                    on { getLatest(BASE_CURRENCY) } doReturn networkError
                }
            }

            lateinit var result: Try<ExchangeRepository.ExchangeRates>
            When("Exchange rates fetched") {
                cut = FixerIoExchangeRepository(client, mapper)
                result = runBlocking { cut.getExchangeRates() }
            }

            Then("API call made") {
                verify(client).getLatest(BASE_CURRENCY)
            }

            Then("Nothing to map") {
                verifyZeroInteractions(mapper)
            }

            Then("Exception is returned") {
                result.fold({ it shouldHaveTheSameClassAs ExchangeRepository.NoDataException() }, { fail() })
            }
        }

        Scenario("Mapper error") {

            Given("Mapping error") {
                client.stub {
                    on { getLatest(BASE_CURRENCY) } doReturn CompletableDeferred(networkResponse)
                }
                mapper.stub {
                    on { invoke(networkResponse) } doThrow RuntimeException()
                }
            }

            When("Exchange rates fetched") {
                cut = FixerIoExchangeRepository(client, mapper)
                assertFailsWith<RuntimeException> {
                    runBlocking { cut.getExchangeRates() }
                }
            }
        }
    }
})
