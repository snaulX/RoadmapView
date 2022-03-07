package com.snaulx.roadmap

import android.graphics.Canvas
import android.graphics.PointF
import androidx.annotation.ColorInt

internal data class PaintLine(val startColumn: PaintBranch, val endColumn: PaintBranch,
                              @ColorInt val color: Int, val width: Float) {
    private val lines: List<Pair<PointF, PointF>>

    init {
        val mutLines = mutableListOf<Pair<PointF, PointF>>()

        val left = endColumn.left
        val endPoints = mutableListOf<PointF>()
        val w = endColumn.columnRect.width()
        for (endPair in endColumn.endPoints) {
            val endPoint: PointF = endPair.first
            endPoint.x += if (left) -w else w
            endPoints.add(endPoint)
        }
        var index = 0 // index for endPoints to add point
        for (startPair in startColumn.endPoints) {
            for (i in 0 until startPair.second) {
                // startPair.second is count of children
                // so iterate count of children and
                // add line to every child from this point
                mutLines.add(startPair.first to endPoints[index++])
            }
        }

        lines = mutLines.toList()
    }

    fun paint(canvas: Canvas) {
        for (line in lines) {
            canvas.drawBezier(line.first, line.second, color, width)
        }
    }
}
