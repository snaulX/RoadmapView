package com.snaulx.roadmap

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import androidx.annotation.ColorInt

internal class PaintBranch(style: BranchStyle, @ColorInt private val textColor: Int,
                       offset: PointF, branches: List<TreeBranch<String>>, val left: Boolean) {
    val columnRect: RectF
    val endPoints: List<Pair<PointF, Int>>

    private val paint = Paint()
    private val textPaint = Paint()

    private val rects: List<List<RectF>>
    private val brValues: List<List<String>>

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

        val mutRects = mutableListOf<List<RectF>>()
        val mutPoints = mutableListOf<Pair<PointF, Int>>()
        val mutValues = mutableListOf<List<String>>()

        val valHeight: Float = rectStyle.height + style.valuesPadding
        val rect = if (left) {
            val offsetTop = offset.y
            val offsetLeft = offset.x
            RectF(
                offsetLeft - rectStyle.width,
                offsetTop,
                offsetLeft,
                offsetTop + rectStyle.height
            )
        } else {
            val offsetTop = offset.y
            val offsetRight = offset.x
            RectF(offsetRight,
                offsetTop,
                offsetRight + rectStyle.width,
                offsetTop + rectStyle.height)
        }
        val endX = if (left) rect.left else rect.right
        columnRect = rect.clone()
        for (branch in branches) {
            val branchList = mutableListOf<RectF>()
            val valuesList = mutableListOf<String>()
            val top = rect.top

            for (v in branch.values) {
                branchList.add(rect.clone())
                valuesList.add(v)

                rect downOn valHeight
            }

            mutValues.add(valuesList)
            mutPoints.add(PointF(endX, (rect.bottom-top)/2) to branch.children.size)
            mutRects.add(branchList.toList())

            rect downOn style.childrenPadding - style.valuesPadding
        }
        columnRect.bottom = rect.bottom + (style.childrenPadding - style.valuesPadding)

        brValues = mutValues.toList()
        endPoints = mutPoints.toList()
        rects = mutRects.toList()
    }

    fun paint(canvas: Canvas) {
        for (i in brValues.indices) {
            val values: List<String> = brValues[i]
            val valRects: List<RectF> = rects[i]
            for (j in values.indices) {
                val rect = valRects[j]
                canvas.drawRoundRect(rect, rx, ry, paint)
                canvas.drawCenterText(values[j], rect, textPaint)
            }
        }
    }
}