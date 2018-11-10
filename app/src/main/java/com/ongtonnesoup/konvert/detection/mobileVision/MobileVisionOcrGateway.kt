package com.ongtonnesoup.konvert.detection.mobileVision

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.text.TextRecognizer
import com.ongtonnesoup.konvert.detection.OcrGateway
import com.ongtonnesoup.konvert.detection.ParsedText
import io.reactivex.Observable
import io.reactivex.ObservableEmitter


class MobileVisionOcrGateway(private val context: Context) : OcrGateway {
    private var detector: OcrDetectorProcessor? = null

    override fun init(): Observable<ParsedText> {
        return Observable.create<ParsedText> { emitter ->
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
            val cameraSource = createCameraSource(textRecognizer) // TODO Pass this to camera preview
        }
    }

    override fun release() {
        detector?.release()
        detector = null
    }

    private fun hasGoogleServices() = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS

    private fun createTextRecognizer(): TextRecognizer? {
        var textRecognizer = TextRecognizer.Builder(context).build()

        fun checkOperational(): Boolean {
            val lowstorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)
            return context.registerReceiver(null, lowstorageFilter) != null
        }

        val isOperational = checkOperational()
        if (!isOperational) {
            textRecognizer = null
        }

        return textRecognizer
    }

    private fun createDetector(emitter: ObservableEmitter<ParsedText>): OcrDetectorProcessor {
        return OcrDetectorProcessor(emitter)
    }

    private fun createCameraSource(textRecognizer: TextRecognizer): CameraSource {
        return CameraSource.Builder(context, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(15.0f)
                .build()
    }
}
