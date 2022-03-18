package com.snaulx.roadmap

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import androidx.annotation.ColorInt

internal class PaintNode(node: TreeNode<String>, @ColorInt textColor: Int, style: NodeStyle, startHeight: Float,
                         canvasWidth: Int, branchStyles: List<BranchStyle>,
                         @ColorInt private val lineColor: Int, private val lineWidth: Float) {

    val nodeRect: RectF
    val rect: RectF

    private val valuesRects: List<RectF>
    private val values: List<String> = node.values

    private val rectStyle: RectStyle = style.style

    private val paintBranches: List<PaintBranch>
    private val paintLines: List<PaintLine>
    private val nodeLines: List<Pair<PointF, PointF>>

    private var drawableBranches = listOf<PaintBranch>()

    private val nodePaint = Paint()
    private val textPaint = Paint()

    init {
        nodePaint.style = Paint.Style.FILL
        nodePaint.color = rectStyle.color
        textPaint.textSize = rectStyle.fontSize
        textPaint.color = textColor

        val w = (canvasWidth-rectStyle.width)/2
        val h = rectStyle.height
        val nodePadding: Float = style.nodePadding
        val mutRect = RectF(w, startHeight, w+rectStyle.width, startHeight+h)
        val mutRects = mutableListOf<RectF>()

        for (value in node.values) {
            mutRects.add(mutRect.clone())
            mutRect downOn h+nodePadding
        }
        nodeRect = RectF(mutRect.left, startHeight, mutRect.right, mutRect.top-nodePadding)
        rect = nodeRect.clone()
        valuesRects = mutRects.toList()

        val table = BranchTable(node, branchStyles)
        val baseRect = nodeRect.clone()
        baseRect.left -= style.branchPadding
        baseRect.right += style.branchPadding
        paintBranches = table.exportPaintBranches(baseRect, textColor)

        val leftPoint = PointF(nodeRect.left, nodeRect.centerY())
        val rightPoint = PointF(nodeRect.right, nodeRect.centerY())
        val mutLines = mutableListOf<Pair<PointF, PointF>>()
        var firstLeft = false
        var firstRight = false
        for (column in paintBranches) {
            // shitty code but it's working
            // trying to find first left and right columns to make lines to them
            val left = if (!firstLeft && column.left) {
                firstLeft = true
                true
            } else if (!firstRight && !column.left) {
                firstRight = true
                false
            } else if (firstLeft && firstRight) {
                break
            } else {
                continue
            }
            val columnWidth = column.columnRect.width()
            for (endPair in column.endPoints) {
                val endPoint: PointF = endPair.first.clone()
                if (left) {
                    endPoint.x += columnWidth
                    mutLines.add(leftPoint to endPoint)
                } else {
                    endPoint.x -= columnWidth
                    mutLines.add(rightPoint to endPoint)
                }
            }
        }
        nodeLines = mutLines.toList()

        calculateDrawableBranches(canvasWidth)
        paintLines = table.exportPaintLines(lineColor, lineWidth)
    }

    private fun calculateDrawableBranches(canvasWidth: Int) {
        val mutBranches = mutableListOf<PaintBranch>()
        for (br in paintBranches) {
            rect.set(maxCombineRect(rect, br.columnRect))
            if (!
                ((rect.left < 0F && br.left) or
                (rect.right > canvasWidth && !br.left))
            )
                mutBranches.add(br)
        }
        drawableBranches = mutBranches.toList()
    }

    /*
    Create new biggest rect from adding rects
     */
    private fun maxCombineRect(a: RectF, b: RectF): RectF {
        val top: Float = if (a.top < b.top) a.top else b.top
        val bottom: Float = if (a.bottom > b.bottom) a.bottom else b.bottom
        val right: Float = if (a.right > b.right) a.right else b.right
        val left: Float = if (a.left < b.left) a.left else b.left
        return RectF(left, top, right, bottom)
    }

    fun paint(canvas: Canvas) {
        for (i in values.indices) {
            val valueRect: RectF = valuesRects[i]
            canvas.drawRoundRect(valueRect, rectStyle.rx, rectStyle.ry, nodePaint)
            canvas.drawCenterText(values[i], valueRect, textPaint)
        }
        for (line in nodeLines) {
            canvas.drawBezier(line.first, line.second, lineColor, lineWidth)
        }
        for (br in drawableBranches) {
            br.paint(canvas)
        }
        for (line in paintLines) {
            line.paint(canvas)
        }
    }
}
