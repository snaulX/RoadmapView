package com.snaulx.roadmap

import android.annotation.SuppressLint
import android.content.Context
import android.gesture.Gesture
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

/**
 * View for showing Roadmap
 */
@SuppressLint("ViewConstructor")
class RoadmapView(context: Context, private val roadmap: PaintTree,
                  private val minScale: Float = 0.1F, private val maxScale: Float = 5F) :
    View(context) {

    private val scrollListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            roadmap.move(distanceX, distanceY)
            invalidate() // redraw to apply changes
            return true
        }
    }
    private val scrollDetector = GestureDetector(context, scrollListener)

    private var scaleFactor = 1F
    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            // don't let the roadmap get too small or too large
            scaleFactor = Math.max(minScale, Math.min(scaleFactor, maxScale))

            invalidate() // redraw to apply changes
            return true
        }
    }
    private val scaleDetector = ScaleGestureDetector(context, scaleListener)

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            save()
            scale(scaleFactor, scaleFactor)
            roadmap.paint(canvas)
            restore()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scrollDetector.onTouchEvent(event)
        scaleDetector.onTouchEvent(event)
        return true
    }
}