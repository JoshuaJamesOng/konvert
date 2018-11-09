package com.ongtonnesoup.konvert.currency.data.local

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldEqual
import org.junit.Test

class SQLiteExchangeRepositoryTest {

    @Test
    fun getExchangeRates() {
        // Given
        val localResponse = listOf(ExchangeRatesDao.ExchangeRate("test", 1.0))
        val dao = mock<ExchangeRatesDao> {
            on { getAll() } doReturn localResponse
        }

        val mapperResponse = ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("test-mapped", 2.0)))
        val localToDomainMapper = mock<(List<ExchangeRatesDao.ExchangeRate>) -> ExchangeRepository.ExchangeRates> {
            on { invoke(localResponse) } doReturn mapperResponse
        }

        val domainToLocalMapper = mock<(ExchangeRepository.ExchangeRates) -> List<ExchangeRatesDao.ExchangeRate>> {}

        // When
        val cut = SQLiteExchangeRepository(dao, domainToLocalMapper, localToDomainMapper)
        val result = runBlocking { cut.getExchangeRates() }

        // Then
        result shouldEqual mapperResponse
        verify(dao).getAll()
        argumentCaptor<List<ExchangeRatesDao.ExchangeRate>>().apply {
            verify(localToDomainMapper).invoke(capture())

            firstValue shouldEqual localResponse
        }
    }

    @Test
    fun getExchangeRatesDaoErrorReturnsDefaultValue() {
        // Given
        val localResponse = listOf(ExchangeRatesDao.ExchangeRate("test", 1.0))
        val dao = mock<ExchangeRatesDao> {
            on { getAll() } doThrow RuntimeException()
        }

        val mapperResponse = ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("test-mapped", 2.0)))
        val localToDomainMapper = mock<(List<ExchangeRatesDao.ExchangeRate>) -> ExchangeRepository.ExchangeRates> {
            on { invoke(localResponse) } doReturn mapperResponse
        }

        val domainToLocalMapper = mock<(ExchangeRepository.ExchangeRates) -> List<ExchangeRatesDao.ExchangeRate>> {}

        // When
        val cut = SQLiteExchangeRepository(dao, domainToLocalMapper, localToDomainMapper)
        val result = runBlocking { cut.getExchangeRates() }

        // Then
        result shouldEqual ExchangeRepository.ExchangeRates(emptyList())

        verify(dao).getAll()
        verifyZeroInteractions(localToDomainMapper)
    }

    @Test
    fun putExchangeRate() {
        // Given
        val rates = ExchangeRepository.ExchangeRates(emptyList())

        val dao = mock<ExchangeRatesDao>()

        val mappedModel = ExchangeRatesDao.ExchangeRate("test-mapped", 2.0)
        val domainToLocalMapper = mock<(ExchangeRepository.ExchangeRates) -> List<ExchangeRatesDao.ExchangeRate>> {
            on { invoke(rates) } doReturn listOf(mappedModel, mappedModel, mappedModel, mappedModel)
        }

        val localToDomainMapper = mock<(List<ExchangeRatesDao.ExchangeRate>) -> ExchangeRepository.ExchangeRates>()

        // When
        val cut = SQLiteExchangeRepository(dao, domainToLocalMapper, localToDomainMapper)
        runBlocking { cut.putExchangeRates(rates) }

        // Then
        verify(dao).clear()
        verify(domainToLocalMapper).invoke(rates)
        verify(dao, times(4)).insert(mappedModel)
    }
}