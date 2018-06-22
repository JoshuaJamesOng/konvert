package com.ongtonnesoup.konvert.state

import com.ongtonnesoup.konvert.di.scopes.PerProcess
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Named

@PerProcess
class AppState @Inject constructor(@Named("defaultState") defaultState: State) {

    private var updates: BehaviorSubject<State> = BehaviorSubject.createDefault(defaultState)

    fun current(): State = updates.value

    fun updates(): Observable<State> = updates.hide()

    fun update(newState: State) = updates.onNext(newState)

}