package com.snaulx.roadmap

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

/**
 * View for showing Roadmap
 */
@SuppressLint("ViewConstructor")
class RoadmapView(context: Context, private val roadmap: PaintTree) :
    View(context) {

    private var contentWidth: Int = 0
    private var contentHeight: Int = 0

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (contentWidth == 0) {
            contentWidth = width - paddingLeft - paddingRight
            contentHeight = height - paddingTop - paddingBottom
        }

        roadmap.paint(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        contentWidth = w - paddingLeft - paddingRight
        contentHeight = h - paddingTop - paddingBottom
    }
}