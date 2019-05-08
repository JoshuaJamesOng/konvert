package com.ongtonnesoup.konvert

import android.os.Parcelable
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.common.plusAssign
import com.ongtonnesoup.common.toSuspendableCompletable
import com.ongtonnesoup.konvert.appupdate.CheckAppUpdateRequired
import com.ongtonnesoup.konvert.common.Dispatchers
import com.ongtonnesoup.konvert.initialisation.CheckLocalRatesAvailable
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.parcel.Parcelize

enum class Availability {
    UNKNOWN,
    UNAVAILABLE,
    AVAILABLE
}

@Parcelize
data class State(
    val updateRequired: Boolean = false,
    val ratesAvailable: Availability = Availability.UNKNOWN
) : BaseState, Parcelable

sealed class Action : BaseAction {
    object CheckUpdate : Action()
    object CheckRates : Action()
    object UpdateComplete : Action()
    object RetryUpdate : Action()
}

sealed class Change {
    object RatesUnavailable : Change()
    object RatesAvailable : Change()
    object UpdateRequired : Change()
    object UpdateComplete : Change()
}

class MainViewModel(
    initialState: State?,
    private val ifAppCheckRequired: CheckAppUpdateRequired,
    private val checkLocalRatesAvailable: CheckLocalRatesAvailable,
    private val dispatchers: Dispatchers
) : BaseViewModel<Action, State>() {

    override val initialState = initialState ?: State()

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.RatesUnavailable -> state.copy(ratesAvailable = Availability.AVAILABLE)
            is Change.RatesAvailable -> state.copy(ratesAvailable = Availability.UNAVAILABLE)
            is Change.UpdateRequired -> state.copy(updateRequired = true)
            is Change.UpdateComplete -> state.copy(updateRequired = false)
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val checkUpdates: Observable<Change> =
            actions.ofType<Action.CheckUpdate>(Action.CheckUpdate::class.java)
                .switchMapSingle {
                    Single.defer { Single.just(ifAppCheckRequired.appUpdateRequired()) }
                        .map { Change.UpdateRequired }
                }

        val watchUpdates: Observable<Change> =
            actions.ofType<Action.UpdateComplete>(Action.UpdateComplete::class.java)
                .map { Change.UpdateComplete }

        val checkRates: Observable<Change> =
            actions.ofType<Action.CheckRates>(Action.CheckRates::class.java)
                .switchMapSingle {
                    toSuspendableCompletable(
                        { checkLocalRatesAvailable.checkLocalRatesAvailable() },
                        dispatchers
                    )
                        .toSingleDefault<Change>(Change.RatesAvailable)
                        .onErrorReturn { Change.RatesUnavailable }
                }

        disposables += Observable.merge(checkUpdates, watchUpdates, checkRates)
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(state::setValue, Timber::e)
    }
}
