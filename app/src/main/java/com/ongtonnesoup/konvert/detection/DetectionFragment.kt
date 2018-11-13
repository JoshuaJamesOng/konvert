package com.ongtonnesoup.konvert.detection

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.ajalt.timberkt.Timber
import com.google.android.gms.vision.CameraSource
import com.ongtonnesoup.konvert.R
import com.ongtonnesoup.konvert.detection.di.DetectionComponent
import com.ongtonnesoup.konvert.detection.di.MobileVisionModule
import com.ongtonnesoup.konvert.detection.mobilevision.MobileVisionOcrGateway
import com.ongtonnesoup.konvert.di.ApplicationComponent
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.detection_fragment.*
import javax.inject.Provider

class DetectionFragment : Fragment(), MobileVisionOcrGateway.View {

    companion object {
        val TAG: String = DetectionFragment::class.java.name
    }

    private val surfaces: Subject<Optional<SurfaceHolder>> = BehaviorSubject.create()
    private val cameraSources: Subject<Optional<CameraSource>> = BehaviorSubject.create()
    private val disposables: CompositeDisposable = CompositeDisposable()

    private lateinit var vm: DetectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val detectionComponent = getDetectionComponent(this)

        val viewModelFactory = DetectionViewModelFactory(detectionComponent)

        vm = ViewModelProviders.of(this, viewModelFactory).get(DetectionViewModel::class.java)

        vm.liveData.observe(this, Observer<DetectionViewModel.UiModel> { uiModel ->
            when (uiModel) {
                is DetectionViewModel.UiModel.Price -> Timber.d { uiModel.toString() }
                is DetectionViewModel.UiModel.Error -> Timber.e { uiModel.toString() }
            }
        })

        listenToSurfaceAndSourceReady()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.detection_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                surfaces.onNext(Optional(holder))
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = Unit

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                surfaces.onNext(Optional())
            }
        })
    }

    override fun onStart() {
        super.onStart()
        vm.startPresenting()
    }

    override fun onStop() {
        vm.stopPresenting()
        super.onStop()
    }

    @SuppressLint("CheckResult")
    private fun listenToSurfaceAndSourceReady() {
        fun <T> Subject<Optional<T>>.presentValuesOnly(): Observable<T> {
            return this.filter { it.data != null }.map { it.data!! }
        }

        val readyUpdates = Observable.zip(
                surfaces.presentValuesOnly(),
                cameraSources.presentValuesOnly(),
                BiFunction<SurfaceHolder, CameraSource, SurfaceAndSource> { surfaceHolder, cameraSource ->
                    SurfaceAndSource(surfaceHolder, cameraSource)
                }
        )

        readyUpdates
                .doOnSubscribe { disposable -> disposables.add(disposable) }
                .subscribe {
                    onSurfaceAndCameraSourceReady(it.surface, it.cameraSource)
                }
    }

    override fun onCameraSourceAvailable(cameraSource: CameraSource) {
        cameraSources.onNext(Optional(cameraSource))
    }

    @SuppressLint("CheckResult", "MissingPermission")
    private fun onSurfaceAndCameraSourceReady(surface: SurfaceHolder, cameraSource: CameraSource) {
        fun onPermissionGranted(function: (Boolean) -> Unit) {
            RxPermissions(this)
                    .request(Manifest.permission.CAMERA)
                    .doOnSubscribe { disposable -> disposables.add(disposable) }
                    .subscribe { granted ->
                        if (granted) {
                            function.invoke(granted)
                        }
                    }
        }

        onPermissionGranted {
            cameraSource.start(surface)
        }
    }

    override fun onCameraSourceReleased() {
        cameraSources.onNext(Optional())
    }
}

private fun getApplicationComponent(fragment: Fragment): ApplicationComponent {
    val provider = fragment.requireActivity().applicationContext as Provider<ApplicationComponent>
    return provider.get()
}

private fun getDetectionComponent(fragment: DetectionFragment): DetectionComponent {
    val applicationComponent = getApplicationComponent(fragment)
    return applicationComponent.getDetectionComponent(MobileVisionModule(fragment))
}

private class Optional<T>(val data: T? = null)

private class SurfaceAndSource(val surface: SurfaceHolder, val cameraSource: CameraSource)
