package com.snaulx.roadmap

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import androidx.annotation.ColorInt

internal class PaintNode(node: TreeNode<String>, @ColorInt textColor: Int, style: NodeStyle, startHeight: Float,
                         canvasWidth: Int, branchStyles: List<BranchStyle>,
                         @ColorInt lineColor: Int, lineWidth: Float) {

    val nodeRect: RectF
    val rect: RectF

    private val valuesRects: List<RectF>
    private val values: List<String> = node.values

    private val rectStyle: RectStyle = style.style

    private val paintBranches: List<PaintBranch>
    private var drawableBranches = listOf<PaintBranch>()
    private val paintLines: List<PaintLine>

    private val leftPoint: PointF
    private val rightPoint: PointF
    private val leftPoints: List<PointF>
    private val rightPoints: List<PointF>

    private val nodePaint = Paint()
    private val textPaint = Paint()
    private val linePaint = Paint()

    private var isMoved = false

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

        leftPoint = PointF(nodeRect.left, nodeRect.centerY())
        rightPoint = PointF(nodeRect.right, nodeRect.centerY())
        val mutLeftPoints = mutableListOf<PointF>()
        val mutRightPoints = mutableListOf<PointF>()
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
                    mutLeftPoints.add(endPoint)
                } else {
                    endPoint.x -= columnWidth
                    mutRightPoints.add(endPoint)
                }
            }
        }
        leftPoints = mutLeftPoints.toList()
        rightPoints = mutRightPoints.toList()

        calculateDrawableBranches(canvasWidth)
        paintLines = table.exportPaintLines(lineColor, lineWidth)

        linePaint.strokeWidth = lineWidth
        linePaint.isAntiAlias = true
        linePaint.style = Paint.Style.STROKE
        linePaint.color = lineColor
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

    fun move(dirX: Float, dirY: Float) {
        rect leftOn dirX
        rect upOn dirY
        nodeRect leftOn dirX
        nodeRect upOn dirY
        for (value in valuesRects) {
            value leftOn dirX
            value upOn dirY
        }
        leftPoint leftOn dirX
        leftPoint upOn dirY
        rightPoint leftOn dirX
        rightPoint upOn dirY
        for (brPoint in leftPoints) {
            brPoint leftOn dirX
            brPoint upOn dirY
        }
        for (brPoint in rightPoints) {
            brPoint leftOn dirX
            brPoint upOn dirY
        }
        for (br in paintBranches) {
            br.move(dirX, dirY)
        }
        for (line in paintLines) {
            line.move(dirX, dirY)
        }
        isMoved = true
    }

    fun paint(canvas: Canvas) {
        if (isMoved) {
            //calculateDrawableBranches(canvas.width)
            isMoved = false
        }
        for (i in values.indices) {
            val valueRect: RectF = valuesRects[i]
            canvas.drawRoundRect(valueRect, rectStyle.rx, rectStyle.ry, nodePaint)
            canvas.drawCenterText(values[i], valueRect, textPaint)
        }
        for (brPoint in leftPoints) {
            canvas.drawBezier(leftPoint, brPoint, linePaint)
        }
        for (brPoint in rightPoints) {
            canvas.drawBezier(rightPoint, brPoint, linePaint)
        }
        for (br in drawableBranches) {
            br.paint(canvas)
        }
        for (line in paintLines) {
            line.paint(canvas)
        }
    }
}
