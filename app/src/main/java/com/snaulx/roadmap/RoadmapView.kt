package com.snaulx.roadmap

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.view.View

/**
 * View for showing Roadmap
 */
@SuppressLint("ViewConstructor")
class RoadmapView(context: Context, private val roadmap: PaintTree) :
    View(context) {

    private var offsetXY = PointF()
    private var scale = 1F

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        roadmap.paint(canvas, offsetXY, scale)
    }
}