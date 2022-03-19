package com.snaulx.roadmap

import android.graphics.PointF

fun PointF.clone(): PointF = PointF(x, y)

infix fun PointF.upOn(y: Float) {
    this.y -= y
}
infix fun PointF.leftOn(x: Float) {
    this.x -= x
}
fun PointF.moveOn(x: Float, y: Float) {
    this.x -= x
    this.y -= y
}