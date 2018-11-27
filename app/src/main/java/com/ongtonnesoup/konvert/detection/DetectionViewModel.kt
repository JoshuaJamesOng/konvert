package com.ongtonnesoup.konvert.detection

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.vision.CameraSource
import com.ongtonnesoup.konvert.detection.di.MobileVisionModule
import com.ongtonnesoup.konvert.detection.mobilevision.MobileVisionOcrGateway
import com.ongtonnesoup.konvert.di.ApplicationComponent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class DetectionViewModel(component: ApplicationComponent) : ViewModel(), MobileVisionOcrGateway.View {

    @Inject
    lateinit var detectPrices: DetectPrices

    private val _liveData = MutableLiveData<UiModel>()
    val liveData: LiveData<UiModel> = _liveData

    private val disposables = CompositeDisposable()

    val cameraSources: Subject<Optional<CameraSource>> = BehaviorSubject.create()
    // TODO Confirm if we can re-se or if this only work because we re-init on resume

    init {
        component.getDetectionComponent(MobileVisionModule(this)).inject(this)
    }

    @SuppressLint("CheckResult")
    fun startPresenting() {
        fun showPrice(price: Number) {
            _liveData.postValue(UiModel.Price(price.text))
        }

        fun showError(error: Throwable) {
            when (error) {
                is OcrGateway.InitializationError -> _liveData.postValue(UiModel.Error)
                else -> {
                    throw error
                }
            }
        }

        detectPrices.detectPrices()
                .doOnSubscribe { disposable -> disposables.add(disposable) }
                .subscribe(::showPrice, ::showError)
    }

    fun stopPresenting() {
        disposables.clear()
    }

    override fun onCameraSourceAvailable(cameraSource: CameraSource) {
        cameraSources.onNext(Optional(cameraSource))
    }

    override fun onCameraSourceReleased() {
        cameraSources.onNext(Optional())
    }

    // TODO Name better
    sealed class UiModel {
        data class Price(val price: String) : UiModel()
        object Error : UiModel()
    }
}

class Optional<T>(val data: T? = null)
