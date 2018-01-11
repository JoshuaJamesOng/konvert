package com.ongtonnesoup.konvert.currency.network

import com.nhaarman.mockito_kotlin.*
import com.ongtonnesoup.konvert.currency.ExchangeRepository
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
        val observer = cut.getExchangeRates().test()

        // Then
        observer.assertComplete()
        observer.assertNoErrors()
        observer.assertValue(mappedResponse)
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
        val observer = cut.getExchangeRates().test()

        // Then
        observer.assertComplete()
        observer.assertNoErrors()
        observer.assertValue(ExchangeRepository.ExchangeRates(emptyList()))
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
        val observer = cut.getExchangeRates().test()

        // Then
        observer.assertComplete()
        observer.assertNoErrors()
        observer.assertValue(ExchangeRepository.ExchangeRates(emptyList()))
        verify(client).getLatest("GBP")
        argumentCaptor<FixerIoClient.FixerIoResponse>().apply {
            verify(mapper).invoke(capture())

            firstValue shouldEqual networkResponse
        }
    }

}