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
import androidx.navigation.fragment.NavHostFragment
import com.github.ajalt.timberkt.Timber
import com.google.android.gms.vision.CameraSource
import com.ongtonnesoup.konvert.R
import com.ongtonnesoup.konvert.detection.mobilevision.MobileVisionOcrGateway
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.detection_fragment.*

@SuppressLint("ValidFragment")
class DetectionFragment(
        private val bundle: Bundle?,
        private val vm: DetectionViewModel
) : Fragment() {

    private val surfaces: Subject<Optional<SurfaceHolder>> = BehaviorSubject.create()
    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        licensesLink.setOnClickListener {
            val showLicenses = DetectionFragmentDirections.actionShowLicenses().apply {
                setFromSettings(false)
            }
            NavHostFragment.findNavController(this).navigate(showLicenses)
        }
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
                vm.cameraSources.presentValuesOnly(),
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
}

private class SurfaceAndSource(val surface: SurfaceHolder, val cameraSource: CameraSource)
