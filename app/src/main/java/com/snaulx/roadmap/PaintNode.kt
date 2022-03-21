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
    private val paintLines: List<PaintLine>

    private val leftPoint: PointF
    private val rightPoint: PointF
    private val leftPoints: List<PointF>
    private val rightPoints: List<PointF>

    private val nodePaint = Paint()
    private val textPaint = Paint()
    private val linePaint = Paint()

    init {
        // Setup paints
        nodePaint.style = Paint.Style.FILL
        nodePaint.color = rectStyle.color
        textPaint.textSize = rectStyle.fontSize
        textPaint.color = textColor
        linePaint.strokeWidth = lineWidth
        linePaint.isAntiAlias = true
        linePaint.style = Paint.Style.STROKE
        linePaint.color = lineColor

        // Init local helper variables
        val w = (canvasWidth-rectStyle.width)/2
        val h = rectStyle.height
        val nodePadding: Float = style.nodePadding
        val mutRect = RectF(w, startHeight, w+rectStyle.width, startHeight+h)
        val mutRects = mutableListOf<RectF>()

        // Generate 'valuesrects' and 'nodeRect'
        for (value in node.values) {
            mutRects.add(mutRect.clone())
            mutRect downOn h+nodePadding
        }
        nodeRect = RectF(mutRect.left, startHeight, mutRect.right, mutRect.top-nodePadding)
        valuesRects = mutRects.toList()

        // Generate BranchTable and 'paintBranches'
        val table = BranchTable(node, branchStyles)
        val baseRect = nodeRect.clone()
        baseRect.left -= style.branchPadding
        baseRect.right += style.branchPadding
        paintBranches = table.exportPaintBranches(baseRect, textColor)

        // Calculate 'rect'
        rect = nodeRect.clone()
        for (br in paintBranches) {
            rect.set(maxCombineRect(rect, br.columnRect))
        }

        // Generate lines from nodes to branches
        leftPoint = PointF(nodeRect.left, nodeRect.centerY())
        rightPoint = PointF(nodeRect.right, nodeRect.centerY())
        val mutLeftPoints = mutableListOf<PointF>()
        val mutRightPoints = mutableListOf<PointF>()
        var firstLeft = false // flag means that we passed first left column
        var firstRight = false // flag means that we passed first right column
        for (column in paintBranches) {
            // shitty code but it's working
            // trying to find first left and right columns to make lines to them
            val left = if (!firstLeft && column.left) {
                firstLeft = true // found first left column
                true
            } else if (!firstRight && !column.left) {
                firstRight = true // found first right column
                false
            } else if (firstLeft && firstRight) {
                break // we found all points that we need
            } else {
                continue // continue finding first columns
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

        // Generate 'paintLines'
        paintLines = table.exportPaintLines(lineColor, lineWidth)
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
        rect.moveOn(dirX, dirY)
        nodeRect.moveOn(dirX, dirY)
        for (value in valuesRects) {
            value.moveOn(dirX, dirY)
        }
        leftPoint.moveOn(dirX, dirY)
        rightPoint.moveOn(dirX, dirY)
        for (brPoint in leftPoints) {
            brPoint.moveOn(dirX, dirY)
        }
        for (brPoint in rightPoints) {
            brPoint.moveOn(dirX, dirY)
        }
        for (br in paintBranches) {
            br.move(dirX, dirY)
        }
        for (line in paintLines) {
            line.move(dirX, dirY)
        }
    }

    fun paint(canvas: Canvas) {
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
        for (br in paintBranches) {
            br.paint(canvas)
        }
        for (line in paintLines) {
            line.paint(canvas)
        }
    }
}
