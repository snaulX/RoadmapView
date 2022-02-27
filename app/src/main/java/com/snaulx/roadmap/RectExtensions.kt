package com.snaulx.roadmap

import android.graphics.RectF

fun RectF.clone(): RectF = RectF(left, top, right, bottom)

infix fun RectF.upOn(value: Float) {
    top -= value
    bottom -= value
}
infix fun RectF.downOn(value: Float) {
    top += value
    bottom += value
}
infix fun RectF.leftOn(value: Float) {
    left -= value
    right -= value
}
infix fun RectF.rightOn(value: Float) {
    left += value
    right += value
}