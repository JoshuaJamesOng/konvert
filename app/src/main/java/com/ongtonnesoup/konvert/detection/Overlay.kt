package com.ongtonnesoup.konvert.detection

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.android.gms.common.images.Size
import com.ongtonnesoup.konvert.BuildConfig
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

private const val POINT_DIAMETER_IN_DP = 24f

typealias Rects = Pair<RectF, Rect>
typealias Detection = Pair<Price, Rects>

class Overlay(context: Context, attributes: AttributeSet) : View(context, attributes) {

    private val _clickedPoints = PublishSubject.create<Price>()
    val clickedPoints: Observable<Price>
        get() = _clickedPoints

    private val detectionPositions: ArrayList<Detection> = arrayListOf()
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

    private var selectedPrice: Detection? = null

    fun setPreviewSize(previewSize: Size) {
        this.previewSize = previewSize

        scaleFactorX = (right - left).toFloat() / previewSize.height
        scaleFactorY = (bottom - top).toFloat() / previewSize.width
    }

    fun showPrices(prices: List<Price>) {
        detectionPositions.clear()

        prices.forEach { price ->
            val point = with(price.position) {
                    val top = top * scaleFactorY
                    val right = right * scaleFactorX
                    RectF(
                        right,
                        top - pointDiameter,
                        right + pointDiameter,
                        top
                    )
                }

            val border = with(price.position) {
                    Rect(
                        (left * scaleFactorX).toInt(),
                        (top * scaleFactorY).toInt(),
                        (right * scaleFactorX).toInt(),
                        (bottom * scaleFactorY).toInt()
                    )
                }

            detectionPositions.add(Detection(price, Rects(point, border)))
            }

        postInvalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val clickedPoint = detectionPositions.asSequence()
                .filter {
                    it.second.first.contains(event.x, event.y)
                }
                .firstOrNull()

            clickedPoint?.let {
                onPointClicked(it)
                return true
            }
        }

        selectedPrice = null
        return super.onTouchEvent(event)
    }

    private fun onPointClicked(price: Detection) {
        selectedPrice = price
        _clickedPoints.onNext(price.first)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        selectedPrice?.let {
            draw(it.second, canvas)
            return
        }

        for ((_, rects) in detectionPositions) {
            draw(rects, canvas)
        }
    }

    private fun draw(rects: Rects, canvas: Canvas) {
        val (point, border) = rects
        canvas.drawOval(point, pointPaint)

        if (BuildConfig.DEBUG) {
            canvas.drawRect(border, borderPaint)
        }
    }
}