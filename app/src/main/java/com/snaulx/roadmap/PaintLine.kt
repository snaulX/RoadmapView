package com.snaulx.roadmap

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import androidx.annotation.ColorInt

internal class PaintLine(startColumn: PaintBranch, endColumn: PaintBranch,
                              @ColorInt color: Int, width: Float) {
    private val lines: List<Pair<PointF, PointF>>
    private val linePaint = Paint()
    private val points: List<PointF>

    init {
        val mutLines = mutableListOf<Pair<PointF, PointF>>()
        val mutPoints = mutableListOf<PointF>()
        val left = endColumn.left
        val endPoints = mutableListOf<PointF>()
        val w = endColumn.columnRect.width()
        for (endPair in endColumn.endPoints) {
            val endPoint: PointF = endPair.first
            endPoint.x += if (left) -w else w
            endPoints.add(endPoint)
            mutPoints.add(endPoint)
        }
        var index = 0 // index for endPoints to add point
        for (startPair in startColumn.endPoints) {
            val startPoint = startPair.first
            mutPoints.add(startPoint)
            for (i in 0 until startPair.second) {
                // startPair.second is count of children
                // so iterate count of children and
                // add line to every child from this point
                mutLines.add(startPoint to endPoints[index++])
            }
        }

        lines = mutLines.toList()
        points = mutPoints.toList()

        linePaint.strokeWidth = width
        linePaint.isAntiAlias = true
        linePaint.style = Paint.Style.STROKE
        linePaint.color = color
    }

    fun move(dirX: Float, dirY: Float) {
        for (point in points) {
            point.moveOn(dirX, dirY)
        }
    }

    fun paint(canvas: Canvas) {
        for (line in lines) {
            canvas.drawBezier(line.first, line.second, linePaint)
        }
    }
}
