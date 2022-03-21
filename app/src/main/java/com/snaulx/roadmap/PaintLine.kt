package com.snaulx.roadmap

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import androidx.annotation.ColorInt

internal class PaintLine(startColumn: PaintBranch, endColumn: PaintBranch, private val paint: Paint) {
    private val lines: List<Pair<PointF, PointF>>
    private val points: List<PointF>

    init {
        val mutLines = mutableListOf<Pair<PointF, PointF>>()
        val mutPoints = mutableListOf<PointF>()

        val left = endColumn.left
        val endPoints = mutableListOf<PointF>()
        val endPointX = if (left) endColumn.columnRect.right else endColumn.columnRect.left
        for (endPair in endColumn.endPoints) {
            val endPoint: PointF = endPair.first.clone()
            endPoint.x = endPointX
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
    }

    fun move(dirX: Float, dirY: Float) {
        for (point in points) {
            point.moveOn(dirX, dirY)
        }
    }

    fun paint(canvas: Canvas) {
        for (line in lines) {
            canvas.drawBezier(line.first, line.second, paint)
        }
    }
}
