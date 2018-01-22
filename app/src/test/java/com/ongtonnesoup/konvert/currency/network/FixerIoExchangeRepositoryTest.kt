package com.ongtonnesoup.konvert.currency.network

import com.nhaarman.mockito_kotlin.*
import com.ongtonnesoup.konvert.currency.ExchangeRepository
import com.rubylichtenstein.rxtest.assertions.should
import com.rubylichtenstein.rxtest.assertions.shouldEmit
import com.rubylichtenstein.rxtest.assertions.shouldHave
import com.rubylichtenstein.rxtest.extentions.test
import com.rubylichtenstein.rxtest.matchers.complete
import com.rubylichtenstein.rxtest.matchers.noErrors
import io.reactivex.Single
import org.amshove.kluent.shouldEqual
import org.junit.Test

class FixerIoExchangeRepositoryTest {

    private lateinit var cut: FixerIoExchangeRepository

    @Test
    fun getExchangeRates() {
        // Given
        val networkResponse = FixerIoClient.FixerIoResponse("base", "date", emptyMap())
        val client = mock<FixerIoClient> {
            on { getLatest("GBP") } doReturn Single.just(networkResponse)
        }

        val mappedResponse = ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("test", 1.0)))
        val mapper = mock<(FixerIoClient.FixerIoResponse) -> ExchangeRepository.ExchangeRates> {
            on { invoke(networkResponse) } doReturn mappedResponse
        }

        // When
        cut = FixerIoExchangeRepository(client, mapper)
        val observable = cut.getExchangeRates()

        // Then
        observable.test {
            it should complete()
            it shouldHave noErrors()
            it shouldEmit mappedResponse
        }
        verify(client).getLatest("GBP")
        argumentCaptor<FixerIoClient.FixerIoResponse>().apply {
            verify(mapper).invoke(capture())

            firstValue shouldEqual networkResponse
        }

    }

    @Test
    fun getExchangeRatesClientErrorReturnsDefaultValue() {
        // Given
        val networkResponse = FixerIoClient.FixerIoResponse("base", "date", emptyMap())
        val client = mock<FixerIoClient> {
            on {getLatest("GBP")} doReturn Single.error<FixerIoClient.FixerIoResponse>(RuntimeException())
        }

        val mappedResponse = ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("test", 1.0)))
        val mapper = mock<(FixerIoClient.FixerIoResponse) -> ExchangeRepository.ExchangeRates> {
            on { invoke(networkResponse) } doReturn mappedResponse
        }

        // When
        cut = FixerIoExchangeRepository(client, mapper)
        val observable = cut.getExchangeRates()

        // Then
        observable.test {
            it should complete()
            it shouldHave noErrors()
            it shouldEmit ExchangeRepository.ExchangeRates(emptyList())
        }
        verify(client).getLatest("GBP")
        verifyZeroInteractions(mapper)
    }

    @Test
    fun getExchangeRatesMapperErrorReturnsDefaultValue() {
        // Given
        val networkResponse = FixerIoClient.FixerIoResponse("base", "date", emptyMap())
        val client = mock<FixerIoClient> {
            on { getLatest("GBP") } doReturn Single.just(networkResponse)
        }

        val mapper = mock<(FixerIoClient.FixerIoResponse) -> ExchangeRepository.ExchangeRates> {
            on { invoke(networkResponse)} doThrow RuntimeException()
        }

        // When
        cut = FixerIoExchangeRepository(client, mapper)
        val observable = cut.getExchangeRates()

        // Then
        observable.test {
            it should complete()
            it shouldHave noErrors()
            it shouldEmit ExchangeRepository.ExchangeRates(emptyList())
        }
        verify(client).getLatest("GBP")
        argumentCaptor<FixerIoClient.FixerIoResponse>().apply {
            verify(mapper).invoke(capture())

            firstValue shouldEqual networkResponse
        }
    }

}