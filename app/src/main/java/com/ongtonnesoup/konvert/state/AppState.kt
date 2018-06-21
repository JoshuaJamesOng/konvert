package com.ongtonnesoup.konvert.state

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AppState @Inject constructor(@Named("defaultState") defaultState: State) {

    private var updates: BehaviorSubject<State> = BehaviorSubject.createDefault(defaultState)

    fun current(): State = updates.value

    fun updates(): Observable<State> = updates.hide()

    fun update(newState: State) = updates.onNext(newState)

}