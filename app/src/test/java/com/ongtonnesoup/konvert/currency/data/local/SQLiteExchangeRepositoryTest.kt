package com.ongtonnesoup.konvert.currency.data.local

import arrow.core.Try
import com.nhaarman.mockitokotlin2.*
import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldHaveTheSameClassAs
import org.junit.Assert.fail
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object SQLiteExchangeRepositoryTest : Spek({
    Feature("Local data") {

        val dao by memoized { mock<ExchangeRatesDao>() }
        val localToDomainMapper by memoized { mock<(List<ExchangeRatesDao.ExchangeRate>) -> ExchangeRepository.ExchangeRates>() }
        val domainToLocalMapper by memoized { mock<(ExchangeRepository.ExchangeRates) -> List<ExchangeRatesDao.ExchangeRate>>() }
        val localResponse = listOf(ExchangeRatesDao.ExchangeRate("test", 1.0))
        val mapperResponse = ExchangeRepository.ExchangeRates(listOf(ExchangeRepository.ExchangeRate("test-mapped", 2.0)))
        val newRates = ExchangeRepository.ExchangeRates(emptyList())

        Scenario("Successful database call") {

            Given("Rates available") {
                dao.stub {
                    on { getAll() } doReturn localResponse
                }
                localToDomainMapper.stub {
                    on { invoke(localResponse) } doReturn mapperResponse
                }
            }

            lateinit var result: Try<ExchangeRepository.ExchangeRates>
            When("Exchange rates fetched") {
                val cut = SQLiteExchangeRepository(dao, domainToLocalMapper, localToDomainMapper)
                result = runBlocking { cut.getExchangeRates() }
            }

            Then("Database queried") {
                verify(dao).getAll()
            }

            Then("Data model mapped to domain model") {
                argumentCaptor<List<ExchangeRatesDao.ExchangeRate>>().apply {
                    verify(localToDomainMapper).invoke(capture())

                    firstValue shouldEqual localResponse
                }
            }

            Then("Mapped response is returned") {
                result shouldEqual Try.just(mapperResponse)
            }
        }

        Scenario("Unsuccessful database calls") {
            Given("Database error") {
                dao.stub {
                    on { getAll() } doThrow RuntimeException()
                }
            }

            lateinit var result: Try<ExchangeRepository.ExchangeRates>
            When("Exchange rates fetched") {
                val cut = SQLiteExchangeRepository(dao, domainToLocalMapper, localToDomainMapper)
                result = runBlocking { cut.getExchangeRates() }
            }

            Then("Database queried") {
                verify(dao).getAll()
            }

            Then("Nothing to map") {
                verifyZeroInteractions(localToDomainMapper)
            }

            Then("Database error is returned") {
                result.fold({ it shouldHaveTheSameClassAs ExchangeRepository.NoDataException() }, { fail() })
            }
        }

        Scenario("Mapper error") {
            Given("Mapping error") {
                dao.stub {
                    on { getAll() } doReturn localResponse
                }
                localToDomainMapper.stub {
                    on { invoke(localResponse) } doThrow RuntimeException()
                }
            }

            lateinit var result: Try<ExchangeRepository.ExchangeRates>
            When("Exchange rates fetched") {
                val cut = SQLiteExchangeRepository(dao, domainToLocalMapper, localToDomainMapper)
                result = runBlocking { cut.getExchangeRates() }
            }

            Then("Database queried") {
                verify(dao).getAll()
            }

            Then("Data model mapped to domain model") {
                verify(localToDomainMapper).invoke(localResponse)
            }

            Then("Mapping error is returned") {
                result.fold({ it shouldHaveTheSameClassAs ExchangeRepository.NoDataException() }, { fail() })
            }
        }

        Scenario("Save exchange rates") {
            lateinit var mappedModel: ExchangeRatesDao.ExchangeRate
            Given("") {
                mappedModel = ExchangeRatesDao.ExchangeRate("test-mapped", 2.0)
                domainToLocalMapper.stub {
                    on { invoke(newRates) } doReturn listOf(mappedModel, mappedModel, mappedModel, mappedModel)
                }
            }

            When("Exchange rates saved") {
                val cut = SQLiteExchangeRepository(dao, domainToLocalMapper, localToDomainMapper)
                runBlocking { cut.putExchangeRates(newRates) }
            }

            Then("Existing data cleared") {
                verify(dao).clear()
            }

            Then("Rates mapped to data model") {
                verify(domainToLocalMapper).invoke(newRates)
            }

            Then("Rates inserted") {
                verify(dao, times(4)).insert(mappedModel)
            }
        }
    }
})
