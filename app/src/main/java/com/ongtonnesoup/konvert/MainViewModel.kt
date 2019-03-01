package com.ongtonnesoup.konvert

import android.os.Parcelable
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.common.plusAssign
import com.ongtonnesoup.common.toSuspendableCompletable
import com.ongtonnesoup.konvert.common.Dispatchers
import com.ongtonnesoup.konvert.initialisation.CheckLocalRatesAvailable
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.parcel.Parcelize

enum class Availability {
    UNKNOWN,
    UNAVAILABLE,
    AVAILABLE
}

@Parcelize
data class State(
        val ratesAvailable: Availability = Availability.UNKNOWN
) : BaseState, Parcelable

sealed class Action : BaseAction {
    object CheckRates : Action()
}

sealed class Change {
    object RatesUnavailable : Change()
    object RatesAvailable : Change()
}

class MainViewModel(
        initialState: State?,
        private val checkLocalRatesAvailable: CheckLocalRatesAvailable,
        private val dispatchers: Dispatchers
) : BaseViewModel<Action, State>() {

    override val initialState = initialState ?: State()

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.RatesUnavailable -> state.copy(ratesAvailable = Availability.AVAILABLE)
            is Change.RatesAvailable -> state.copy(ratesAvailable = Availability.UNAVAILABLE)
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val checkRates: Observable<Change> = actions.ofType<Action.CheckRates>(Action.CheckRates::class.java)
                .switchMapSingle {
                    toSuspendableCompletable({ checkLocalRatesAvailable.checkLocalRatesAvailable() }, dispatchers)
                            .toSingleDefault<Change>(Change.RatesAvailable)
                            .onErrorReturn { Change.RatesUnavailable }
                }

        disposables += checkRates
                .scan(initialState, reducer)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(state::setValue, Timber::e)
    }
}
