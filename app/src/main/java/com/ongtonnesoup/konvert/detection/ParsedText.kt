package com.ongtonnesoup.konvert.detection

import android.graphics.Rect
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetectionPosition(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) : Parcelable {
    companion object {
        fun fromRect(rect: Rect) =
            DetectionPosition(
                rect.left,
                rect.top,
                rect.right,
                rect.bottom
            )
    }
}

data class ParsedText(
    val text: String,
    val position: DetectionPosition
)
