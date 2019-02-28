package com.ongtonnesoup.konvert.detection

import android.os.Parcelable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.github.ajalt.timberkt.Timber
import com.google.android.gms.vision.CameraSource
import com.ongtonnesoup.common.plusAssign
import com.ongtonnesoup.konvert.detection.di.MobileVisionModule
import com.ongtonnesoup.konvert.detection.mobilevision.MobileVisionOcrGateway
import com.ongtonnesoup.konvert.di.ApplicationComponent
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

sealed class State : BaseState, Parcelable {
    @Parcelize object Idle : State()
    @Parcelize object Ready : State()
    @Parcelize data class Price(val price: String) : State()
    @Parcelize object Error : State()
}

sealed class Action : BaseAction {
    object OnViewAvailable : Action()
    object OnViewUnavailable : Action()
    object Ready : Action()
    data class PriceDetected(val price: String) : Action()
    data class Error(val throwable: Throwable) : Action()
}

sealed class Change {
    data class ShowPrice(val price: String) : Change()
}

class DetectionViewModel(
        initialState: State?,
        component: ApplicationComponent
) : BaseViewModel<Action, State>(), LifecycleObserver, MobileVisionOcrGateway.View {

    @Inject
    lateinit var detectPrices: DetectPrices

    override val initialState = initialState ?: State.Idle

    val cameraSources: Subject<Optional<CameraSource>> = BehaviorSubject.create()
    // TODO Confirm if we can re-se or if this only work because we re-init on resume

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.ShowPrice -> State.Price(change.price)
        }
    }

    init {
        inject(component)
        bindActions()
    }

    private fun inject(component: ApplicationComponent) {
        component.getDetectionComponent(MobileVisionModule(this))
                .inject(this)
    }

    private fun bindActions() {
        val showPriceChange: Observable<Change> = actions.ofType<Action.PriceDetected>(Action.PriceDetected::class.java)
                .switchMap {
                    Observable.just(Change.ShowPrice(it.price))
                }

        disposables += showPriceChange
                .scan(initialState, reducer)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(state::setValue, Timber::e)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun detectPrices() {
        disposables += detectPrices.detectPrices()
                .subscribe({ price -> dispatch(Action.PriceDetected(price.text)) }, { error -> dispatch(Action.Error(error)) })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopDetection() {
        if (cameraSources is BehaviorSubject) {
            Timber.d { "Manually releasing source" }
            cameraSources.value?.data?.release()
        }
    }

    override fun onCameraSourceAvailable(cameraSource: CameraSource) {
        cameraSources.onNext(Optional(cameraSource))
    }

    override fun onCameraSourceReleased() {
        cameraSources.onNext(Optional())
    }
}

// TODO Use Arrow
class Optional<T>(val data: T? = null)
