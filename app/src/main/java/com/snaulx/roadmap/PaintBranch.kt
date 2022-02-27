package com.snaulx.roadmap

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.ColorInt

data class PaintBranch(val style: BranchStyle, @ColorInt val textColor: Int,
                       val offset: RectF, val branches: List<List<String>>) {
    val columnRect: RectF

    private val paint = Paint()
    private val textPaint = Paint()
    private val rects: List<List<RectF>>

    private val rx: Float
    private val ry: Float

    init {
        paint.style = Paint.Style.FILL
        paint.color = style.style.color
        textPaint.textSize = style.style.fontSize
        textPaint.color = textColor
        val rectStyle: RectStyle = style.style
        rx = rectStyle.rx
        ry = rectStyle.ry

        val valHeight: Float = rectStyle.height + style.valuesPadding
        val mutRects = mutableListOf<List<RectF>>()
        val offsetLeft = offset.left
        val offsetTop = offset.top
        val rect = RectF(offsetLeft - rectStyle.width, offsetTop, offsetLeft, offsetTop + rectStyle.height)
        columnRect = rect.clone()
        for (branch in branches) {
            val branchList = mutableListOf<RectF>()
            for (i in branch.indices) {
                branchList.add(rect.clone())
                rect downOn valHeight
            }
            mutRects.add(branchList.toList())
            rect downOn style.childrenPadding - style.valuesPadding
        }
        columnRect.bottom = rect.bottom + (style.childrenPadding - style.valuesPadding)
        rects = mutRects.toList()
    }

    fun paint(canvas: Canvas) {
        for (i in branches.indices) {
            val values: List<String> = branches[i]
            val valRects: List<RectF> = rects[i]
            for (j in values.indices) {
                val rect = valRects[j]
                canvas.drawRoundRect(rect, rx, ry, paint)
                canvas.drawCenterText(values[j], rect, textPaint)
            }
        }
    }
}