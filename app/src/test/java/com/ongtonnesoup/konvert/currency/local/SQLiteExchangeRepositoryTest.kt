package com.ongtonnesoup.konvert.currency.local

import com.nhaarman.mockito_kotlin.*
import com.ongtonnesoup.konvert.currency.ExchangeRepository
import io.reactivex.Flowable
import org.amshove.kluent.shouldEqual
import org.junit.Test

class SQLiteExchangeRepositoryTest {

    @Test
    fun getExchangeRates() {
        // Given
        val localResponse = listOf(ExchangeRatesDao.ExchangeRate("test", 1.0))
        val dao = mock<ExchangeRatesDao> {
            on { getAll() } doReturn Flowable.just(localResponse)
        }

        val mapperResponse = ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("test-mapped", 2.0)))
        val localToDomainMapper = mock<(List<ExchangeRatesDao.ExchangeRate>) -> ExchangeRepository.ExchangeRates> {
            on { invoke(localResponse) } doReturn mapperResponse
        }

        val domainToLocalMapper = mock<(ExchangeRepository.ExchangeRates) -> List<ExchangeRatesDao.ExchangeRate>> {}

        // When
        val cut = SQLiteExchangeRepository(dao, domainToLocalMapper, localToDomainMapper)
        val observer = cut.getExchangeRates().test()

        // Then
        observer.assertComplete()
        observer.assertNoErrors()
        observer.assertValue(mapperResponse)
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
            on { getAll() } doReturn  Flowable.error<List<ExchangeRatesDao.ExchangeRate>>(RuntimeException())
        }

        val mapperResponse = ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("test-mapped", 2.0)))
        val localToDomainMapper = mock<(List<ExchangeRatesDao.ExchangeRate>) -> ExchangeRepository.ExchangeRates> {
            on { invoke(localResponse) } doReturn mapperResponse
        }

        val domainToLocalMapper = mock<(ExchangeRepository.ExchangeRates) -> List<ExchangeRatesDao.ExchangeRate>> {}

        // When
        val cut = SQLiteExchangeRepository(dao, domainToLocalMapper, localToDomainMapper)
        val observer = cut.getExchangeRates().test()

        // Then
        observer.assertComplete()
        observer.assertNoErrors()
        observer.assertValue(ExchangeRepository.ExchangeRates(emptyList()))
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
        val observer = cut.putExchangeRates(rates).test()

        // Then
        observer.assertComplete()
        observer.assertNoErrors()
        verify(dao).clear()
        verify(domainToLocalMapper).invoke(rates)
        verify(dao, times(4)).insert(mappedModel)
    }
}