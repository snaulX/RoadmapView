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
import android.view.View

/**
 * View for showing Roadmap
 */
@SuppressLint("ViewConstructor")
class RoadmapView(context: Context, private val roadmap: PaintTree) :
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

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        roadmap.paint(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return scrollDetector.onTouchEvent(event)
    }
}