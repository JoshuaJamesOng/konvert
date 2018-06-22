package com.ongtonnesoup.konvert.initialisation

import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.currency.domain.GetCurrentDataState
import com.ongtonnesoup.konvert.currency.domain.UpdateExchangeRates
import com.ongtonnesoup.konvert.currency.refresh.ScheduleRefresh
import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.DataState
import com.ongtonnesoup.konvert.state.updateDataState
import com.ongtonnesoup.konvert.state.updateInitialisedState
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class InitialiseApp @Inject constructor(
        private val getCurrentDataState: GetCurrentDataState,
        private val updateExchangeRates: UpdateExchangeRates,
        private val scheduleRefresh: ScheduleRefresh,
        private val appState: AppState
) {

    fun initialise(): Completable {
        return getLocalDataState()
                .doOnSuccess { dataState -> updateDataState(appState, dataState) }
                .flatMapCompletable { status ->
                    when (status) {
                        DataState.NO_DATA -> fetchNowThenScheduleRefresh()
                        DataState.CACHED_DATA -> scheduleRefresh()
                        else -> {
                            throw IllegalStateException(status.name)
                        }
                    }
                }
                .doOnComplete { updateInitialisedState(appState, true) }
    }

    private fun getLocalDataState(): Single<DataState> {
        return getCurrentDataState.load()
    }

    private fun fetchNowThenScheduleRefresh(): Completable {
        return updateExchangeRates.getExchangeRates()
                .doOnSubscribe { Timber.d { "No data. Fetching exchange rates" } }
                .doOnComplete { Timber.d { "Scheduling job after force fetch" } }
                .andThen(scheduleRefresh())
    }

    private fun scheduleRefresh(): Completable {
        return scheduleRefresh.scheduleRefresh()
                .doOnSubscribe { Timber.d { "Scheduling Job" } }
    }

}