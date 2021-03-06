package com.ongtonnesoup.konvert.detection.mobilevision

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.text.TextRecognizer
import com.ongtonnesoup.konvert.detection.OcrGateway
import com.ongtonnesoup.konvert.detection.ParsedText
import com.ongtonnesoup.konvert.di.qualifiers.ContextType
import com.ongtonnesoup.konvert.di.qualifiers.Type
import com.ongtonnesoup.konvert.di.scopes.PerFragment
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import javax.inject.Inject

@PerFragment
class MobileVisionOcrGateway @Inject constructor(
    @ContextType(Type.APPLICATION) private val context: Context,
    private val view: View
) : OcrGateway {

    private var detector: OcrDetectorProcessor? = null

    @Suppress("ReturnCount")
    override fun init(): Observable<List<ParsedText>> {
        return Observable.create { emitter ->
            if (!hasGoogleServices()) {
                emitter.onError(OcrGateway.InitializationError())
                return@create
            }

            val textRecognizer = createTextRecognizer()

            if (textRecognizer == null) {
                emitter.onError(OcrGateway.InitializationError())
                return@create
            }

            detector = createDetector(emitter)
            textRecognizer.setProcessor(detector)
            val cameraSource = createCameraSource(textRecognizer)

            view.onCameraSourceAvailable(cameraSource)
        }
    }

    override fun release() {
        detector?.release()
        detector = null
        view.onCameraSourceReleased()
    }

    private fun hasGoogleServices() = GoogleApiAvailability.getInstance()
        .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS

    private fun createTextRecognizer(): TextRecognizer? {
        var textRecognizer = TextRecognizer.Builder(context).build()

        fun checkOperational(): Boolean {
//            val lowStorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)
//            return textRecognizer.isOperational && context.registerReceiver(null, lowStorageFilter) != null
            return textRecognizer.isOperational
        }

        val isOperational = checkOperational()
        if (!isOperational) {
            textRecognizer = null
        }

        return textRecognizer
    }

    private fun createDetector(emitter: ObservableEmitter<List<ParsedText>>): OcrDetectorProcessor {
        return OcrDetectorProcessor(emitter)
    }

    private fun createCameraSource(textRecognizer: TextRecognizer): CameraSource {
        return CameraSource.Builder(context, textRecognizer)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(1280, 1024)
            .setRequestedFps(15.0f)
            .setAutoFocusEnabled(true)
            .build()
    }

    interface View {

        fun onCameraSourceAvailable(cameraSource: CameraSource)

        fun onCameraSourceReleased()
    }
}
