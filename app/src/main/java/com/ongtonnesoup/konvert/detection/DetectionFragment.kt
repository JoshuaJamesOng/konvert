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
import com.ongtonnesoup.konvert.android.getApplicationComponent
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.detection_fragment.*
import com.ongtonnesoup.konvert.android.BUNDLE_KEY_SAVED_VIEWMODEL_STATE as SAVED_STATE

@SuppressLint("ValidFragment")
class DetectionFragment : Fragment() {

    companion object {
        val TAG: String = DetectionFragment::class.java.name
    }

    private val disposables: CompositeDisposable = CompositeDisposable()

    private lateinit var viewModel: DetectionViewModel
    private var surface: SurfaceHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialState: State? = savedInstanceState?.getParcelable(SAVED_STATE) ?: State.Idle
        viewModel = ViewModelProviders.of(this, DetectionViewModelFactory(initialState, getApplicationComponent(this))).get(DetectionViewModel::class.java)

        viewModel.observableState.observe(this, Observer<State> { uiModel ->
            when (uiModel) {
                is State.Idle -> Timber.d { uiModel.toString() }
                is State.Ready -> onSurfaceAndCameraSourceReady(surface!!, uiModel.cameraSource) // TODO Bang bang
                is State.Price -> Timber.d { uiModel.toString() }
                is State.Error -> Timber.e { uiModel.toString() }
            }
        })

        lifecycle.addObserver(viewModel)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SAVED_STATE, viewModel.observableState.value)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.detection_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                surface = holder
                viewModel.dispatch(Action.CameraAvailable)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = Unit

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                surface = null
            }
        })
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
