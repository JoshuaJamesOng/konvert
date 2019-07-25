package com.ongtonnesoup.konvert.detection

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.google.android.gms.common.images.Size
import com.ongtonnesoup.konvert.BuildConfig

private const val POINT_DIAMETER_IN_DP = 24f

class Overlay(context: Context, attributes: AttributeSet) : View(context, attributes) {

    private val detectionPositions: ArrayList<Pair<RectF, Rect>> = arrayListOf()
    private val borderPaint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val pointPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    private val pointDiameter by lazy {
        POINT_DIAMETER_IN_DP * Resources.getSystem().displayMetrics.density
    }
    private var previewSize: Size? = null
    private var scaleFactorX: Float = 1f
    private var scaleFactorY: Float = 1f

    fun setPreviewSize(previewSize: Size) {
        this.previewSize = previewSize

        scaleFactorX = (right - left).toFloat() / previewSize.height
        scaleFactorY = (bottom - top).toFloat() / previewSize.width
    }

    fun showPrices(prices: List<Price>) {
        detectionPositions.clear()

        prices.map { it.position }
            .forEach { position ->
                val point = with(position) {
                    val top = top * scaleFactorY
                    val right = right * scaleFactorX
                    RectF(
                        right,
                        top - pointDiameter,
                        right + pointDiameter,
                        top
                    )
                }

                val border = with(position) {
                    Rect(
                        (left * scaleFactorX).toInt(),
                        (top * scaleFactorY).toInt(),
                        (right * scaleFactorX).toInt(),
                        (bottom * scaleFactorY).toInt()
                    )
                }

                detectionPositions.add(Pair(point, border))
            }

        postInvalidate()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        for ((point, border) in detectionPositions) {
            canvas.drawOval(point, pointPaint)

            if (BuildConfig.DEBUG) {
                canvas.drawRect(border, borderPaint)
            }
        }
    }
}