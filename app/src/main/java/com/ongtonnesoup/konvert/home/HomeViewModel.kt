package com.ongtonnesoup.konvert.home

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.common.plusAssign
import com.ongtonnesoup.konvert.common.Event
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.parcel.Parcelize

@Parcelize
data class State(
    val showCameraView: Boolean = false
) : BaseState, Parcelable

sealed class Action : BaseAction {
    object OcrSupported : Action()
    object ShowSettings : Action()
}

sealed class Effect {
    object ShowSettings : Effect()
}

sealed class Change {
    object ShowOcr : Change()
}

class HomeViewModel(
    initialState: State?
) : BaseViewModel<Action, State>() {

    override val initialState = initialState ?: State()

    private val _observableEffects = MutableLiveData<Event<Effect>>()
    val observableEffects: LiveData<Event<Effect>> get() = _observableEffects

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.ShowOcr -> state.copy(showCameraView = true)
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val showOcrChange: Observable<Change> =
            actions.ofType<Action.OcrSupported>(Action.OcrSupported::class.java)
                .switchMap {
                    Observable.just(Change.ShowOcr)
                }

        disposables += actions.ofType<Action.ShowSettings>(Action.ShowSettings::class.java)
            .observeOn(AndroidSchedulers.mainThread())
            .map { Effect.ShowSettings }
            .subscribe({ effect -> _observableEffects.postValue(Event(effect)) }, Timber::e)

        disposables += showOcrChange
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(state::setValue, Timber::e)
    }
}
