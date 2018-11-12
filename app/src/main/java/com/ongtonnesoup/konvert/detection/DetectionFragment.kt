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
import com.ongtonnesoup.konvert.detection.mobileVision.MobileVisionOcrGateway
import com.ongtonnesoup.konvert.di.ApplicationComponent
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.detection_fragment.*
import javax.inject.Provider

class DetectionFragment : Fragment(), MobileVisionOcrGateway.View {

    companion object {
        val TAG: String = DetectionFragment::class.java.name
    }

    private val disposables: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private lateinit var vm: DetectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val detectionComponent = getDetectionComponent(this)

        val viewModelFactory = DetectionViewModelFactory(detectionComponent)

        vm = ViewModelProviders.of(this, viewModelFactory).get(DetectionViewModel::class.java)

        vm.liveData.observe(this, Observer<DetectionViewModel.UiModel> {
            when (it) {
                is DetectionViewModel.UiModel.Price -> Timber.d { it.toString() }
                is DetectionViewModel.UiModel.Error -> Timber.e { it.toString() }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.detection_fragment, container, false)
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
    override fun onCameraSourceAvailable(cameraSource: CameraSource) {
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
            showCameraPreview(cameraSource)
        }
    }

    private fun showCameraPreview(cameraSource: CameraSource) {
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                cameraSource.start(holder)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })
    }
}

private fun getApplicationComponent(fragment: Fragment): ApplicationComponent {
    val provider = fragment.requireActivity().applicationContext as Provider<ApplicationComponent>
    return provider.get()
}

private fun getDetectionComponent(fragment: DetectionFragment) : DetectionComponent {
    val applicationComponent = getApplicationComponent(fragment)
    return applicationComponent.getDetectionComponent(MobileVisionModule(fragment))
}
