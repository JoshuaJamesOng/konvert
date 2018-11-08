package com.ongtonnesoup.konvert.currency.data.network

import com.nhaarman.mockito_kotlin.*
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldEqual
import org.junit.Test
import java.io.IOException

class FixerIoExchangeRepositoryTest {

    private lateinit var cut: FixerIoExchangeRepository

    @Test
    fun getExchangeRates() {
        // Given
        val networkResponse = FixerIoClient.Response("base", "date", emptyMap())
        val client = mock<FixerIoClient> {
            on { getLatest("GBP") } doReturn CompletableDeferred(networkResponse)
        }

        val mappedResponse = ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("test", 1.0)))
        val mapper = mock<(FixerIoClient.Response) -> ExchangeRepository.ExchangeRates> {
            on { invoke(networkResponse) } doReturn mappedResponse
        }

        // When
        cut = FixerIoExchangeRepository(client, mapper)
        val result = runBlocking { cut.getExchangeRates() }

        // Then
        result shouldEqual mappedResponse
        verify(client).getLatest("GBP")
        argumentCaptor<FixerIoClient.Response>().apply {
            verify(mapper).invoke(capture())

            firstValue shouldEqual networkResponse
        }
    }

    @Test
    fun getExchangeRatesClientErrorReturnsDefaultValue() {
        // Given
        val networkError = CompletableDeferred<FixerIoClient.Response>()
        networkError.completeExceptionally(IOException())

        val client = mock<FixerIoClient> {
            on { getLatest("GBP") } doReturn networkError
        }

        val mapper = mock<(FixerIoClient.Response) -> ExchangeRepository.ExchangeRates>()

        // When
        cut = FixerIoExchangeRepository(client, mapper)
        val result = runBlocking { cut.getExchangeRates() }

        // Then
        result shouldEqual ExchangeRepository.ExchangeRates(emptyList())
        verify(client).getLatest("GBP")
        verifyZeroInteractions(mapper)
    }

    @Test(expected = RuntimeException::class)
    fun getExchangeRatesMapperErrorThrows() {
        // Given
        val networkResponse = FixerIoClient.Response("base", "date", emptyMap())
        val client = mock<FixerIoClient> {
            on { getLatest("GBP") } doReturn CompletableDeferred(networkResponse)
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