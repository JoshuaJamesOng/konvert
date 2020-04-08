package com.ongtonnesoup.konvert.detection.mobilevision

import android.util.SparseArray
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.ongtonnesoup.konvert.detection.DetectionPosition
import com.ongtonnesoup.konvert.detection.ParsedText
import io.reactivex.ObservableEmitter

class OcrDetectorProcessor(
    private val emitter: ObservableEmitter<List<ParsedText>>
) : Detector.Processor<TextBlock> {

    override fun receiveDetections(detections: Detector.Detections<TextBlock>) =
        emitter.onNext(
            detections.detectedItems.toList()
                .filter { textBlock -> textBlock.value != null }
                .map { textBlock ->
                    ParsedText(
                        textBlock.value,
                        DetectionPosition.fromRect(textBlock.boundingBox)
                    )
                }
        )

    override fun release() = Unit
}

private fun <T> SparseArray<T>.toList(): List<T> {
    val list = mutableListOf<T>()
    for (i in 0 until this.size()) {
        val value = this.valueAt(i)
        if (value != null) {
            list.add(value)
        }
    }
    return list
}
