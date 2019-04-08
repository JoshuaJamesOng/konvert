package com.ongtonnesoup.konvert.currency.data.network

import arrow.core.Try
import com.nhaarman.mockitokotlin2.*
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import junit.framework.Assert.fail
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldHaveTheSameClassAs
import org.junit.Test
import java.io.IOException

private const val BASE_CURRENCY = "GBP"

class FixerIoExchangeRepositoryTest {

    private lateinit var cut: FixerIoExchangeRepository

    @Test
    fun getExchangeRates() {
        // Given
        val networkResponse = FixerIoClient.Response("base", "date", emptyMap())
        val client = mock<FixerIoClient> {
            on { getLatest(BASE_CURRENCY) } doReturn CompletableDeferred(networkResponse)
        }

        val mappedResponse = ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("test", 1.0)))
        val mapper = mock<(FixerIoClient.Response) -> ExchangeRepository.ExchangeRates> {
            on { invoke(networkResponse) } doReturn mappedResponse
        }

        // When
        cut = FixerIoExchangeRepository(client, mapper)
        val result = runBlocking { cut.getExchangeRates() }

        // Then
        result shouldEqual Try.just(mappedResponse)
        verify(client).getLatest(BASE_CURRENCY)
        argumentCaptor<FixerIoClient.Response>().apply {
            verify(mapper).invoke(capture())

            firstValue shouldEqual networkResponse
        }
    }

    @Test
    fun raisesNetworkError() {
        // Given
        val networkError = CompletableDeferred<FixerIoClient.Response>()
        networkError.completeExceptionally(IOException())

        val client = mock<FixerIoClient> {
            on { getLatest(BASE_CURRENCY) } doReturn networkError
        }

        val mapper = mock<(FixerIoClient.Response) -> ExchangeRepository.ExchangeRates>()

        // When
        cut = FixerIoExchangeRepository(client, mapper)
        val result = runBlocking { cut.getExchangeRates() }

        // Then
        result.fold({ it shouldHaveTheSameClassAs ExchangeRepository.NoDataException() }, { fail() })
        verify(client).getLatest(BASE_CURRENCY)
        verifyZeroInteractions(mapper)
    }

    @Test(expected = RuntimeException::class)
    fun getExchangeRatesMapperErrorThrows() {
        // Given
        val networkResponse = FixerIoClient.Response("base", "date", emptyMap())
        val client = mock<FixerIoClient> {
            on { getLatest(BASE_CURRENCY) } doReturn CompletableDeferred(networkResponse)
        }

        val mapper = mock<(FixerIoClient.Response) -> ExchangeRepository.ExchangeRates> {
            on { invoke(networkResponse) } doThrow RuntimeException()
        }

        // When
        cut = FixerIoExchangeRepository(client, mapper)
        runBlocking { cut.getExchangeRates() }

        // Then
        // Throws
    }
}
