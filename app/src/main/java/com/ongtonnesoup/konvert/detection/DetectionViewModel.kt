package com.ongtonnesoup.konvert.detection

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
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
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

sealed class State : BaseState, Parcelable {
    @Parcelize
    object Idle : State()

    @Parcelize
    object WaitingForCameraSource : State()

    @Parcelize
    data class Ready(val cameraSource: CameraSource?) : State() {
        private companion object : Parceler<Ready> {
            override fun Ready.write(parcel: Parcel, flags: Int) = Unit

            override fun create(parcel: Parcel): Ready = Ready(null)
        }
    } // TODO Check this stuff

    @Parcelize
    data class Price(val price: String) : State()

    @Parcelize
    object Error : State()
}

sealed class Action : BaseAction {
    object OnViewAvailable : Action()
    object OnViewUnavailable : Action()
    object CameraAvailable : Action() // TODO Rename
    data class PriceDetected(val price: String) : Action()
    data class Error(val throwable: Throwable) : Action()
}

sealed class Change {
    object WaitingForCameraSource : Change()
    data class CameraAvailable(val cameraSource: CameraSource) : Change()
    data class ShowPrice(val price: String) : Change()
}

class DetectionViewModel(
    initialState: State?,
    component: ApplicationComponent
) : BaseViewModel<Action, State>(), DefaultLifecycleObserver, MobileVisionOcrGateway.View {

    @Inject
    lateinit var detectPrices: DetectPrices

    override val initialState = initialState ?: State.Idle

    private val internalChanges: Subject<Change> = PublishSubject.create()
    private var cameraSource: CameraSource? = null

    private val reducer: Reducer<State, Change> = { _, change ->
        when (change) {
            is Change.ShowPrice -> State.Price(change.price)
            is Change.WaitingForCameraSource -> State.WaitingForCameraSource
            is Change.CameraAvailable -> State.Ready(change.cameraSource)
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
        val showPriceChange: Observable<Change> =
            actions.ofType<Action.PriceDetected>(Action.PriceDetected::class.java)
                .switchMap {
                    Observable.just(Change.ShowPrice(it.price))
                }

        val cameraAvailable: Observable<Change> =
            actions.ofType<Action.CameraAvailable>(Action.CameraAvailable::class.java)
                .map { Optional(cameraSource) }
                .switchMap {
                    if (it.data == null) {
                        Observable.just(Change.WaitingForCameraSource)
                    } else {
                        Observable.just(Change.CameraAvailable(it.data))
                    }
                }

        disposables += Observable.merge(showPriceChange, cameraAvailable, internalChanges)
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(state::setValue, Timber::e)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        detectPrices()
    }

    private fun detectPrices() {
        disposables += detectPrices.detectPrices()
            .subscribe(
                { price -> dispatch(Action.PriceDetected(price.text)) },
                { error -> dispatch(Action.Error(error)) }
            )
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        stopDetection()
    }

    private fun stopDetection() {
        cameraSource?.release()
    }

    override fun onCameraSourceAvailable(cameraSource: CameraSource) {
        this.cameraSource = cameraSource
    }

    override fun onCameraSourceReleased() {
        cameraSource = null
        internalChanges.onNext(Change.WaitingForCameraSource)
    }
}

// TODO Use Arrow
class Optional<T>(val data: T? = null)
