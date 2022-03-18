package com.snaulx.roadmap

import android.graphics.*
import androidx.annotation.ColorInt

fun Canvas.drawCenterText(value: String, rect: RectF, textPaint: Paint) {
    val textBounds = Rect()
    textPaint.getTextBounds(value, 0, value.length, textBounds)
    drawText(value, rect.left + (rect.width() - textBounds.width())/2,
        rect.centerY() + textBounds.height()/2, textPaint)
}

// https://stackoverflow.com/questions/30073682/how-to-draw-bezier-curve-in-android
fun Canvas.drawBezier(start: PointF, end: PointF, paint: Paint) {
    val p = Path()
    val mid = PointF()
    mid.set((start.x + end.x) / 2, (start.y + end.y) / 2)

    // Draw line connecting the two points:
    p.reset()
    p.moveTo(start.x, start.y)
    p.quadTo((start.x + mid.x) / 2, start.y, mid.x, mid.y)
    p.quadTo((mid.x + end.x) / 2, end.y, end.x, end.y)

    drawPath(p, paint)
}

fun Canvas.drawBezier(start: PointF, end: PointF, @ColorInt color: Int, width: Float) {
    val pLine: Paint = object : Paint() {
        init {
            style = Style.STROKE
            isAntiAlias = true
            strokeWidth = width
            this.color = color
        }
    }

    drawBezier(start, end, pLine)
}