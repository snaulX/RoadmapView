package com.snaulx.roadmap

import android.graphics.*
import androidx.annotation.ColorInt

data class PaintTree(val tree: Tree<String>, val style: TreeStyle) {

    private val basePadding = style.padding

    private val nodeStyle: RectStyle = style.nodeStyle.style
    private val nodeHeight: Float = nodeStyle.height
    private val nodePadding: Float = style.nodeStyle.nodePadding
    private val nodePaint = Paint()

    private val branchStyles: List<BranchStyle> = style.branchStyles.toList()

    private val textPaint = Paint()
    private val paintLine = Paint()

    private var lastHeight = basePadding

    init {
        nodePaint.style = Paint.Style.FILL
        nodePaint.color = nodeStyle.color
        textPaint.textSize = nodeStyle.fontSize
        textPaint.color = style.textColor
        paintLine.color = Color.rgb(250, 174, 53)
        paintLine.strokeWidth = 7F
    }

    // !!! YOU CAN OPTIMISE IT !!!
    // You can calculate all in constructor and cache it
    // Like in PaintBranch class
    fun paint(canvas: Canvas, offsetXY: PointF, scale: Float) {
        val w = canvas.width
        val midX: Float = (w + offsetXY.x) / 2
        val startPoint = PointF(midX, offsetXY.y)
        lastHeight += offsetXY.y
        val endPoint = PointF(midX, lastHeight)

        for (node in tree.body) {
            val table = BranchTable(node, branchStyles)

            endPoint.y = lastHeight
            canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paintLine)
            var rect = paintNode(canvas, node)
            startPoint.y = rect.bottom

            val brPadding = style.nodeStyle.branchPadding
            rect.left -= brPadding
            rect.right += brPadding
            val branches: List<PaintBranch> = table.exportPaintBranches(rect, style.textColor)
            for (br in branches) {
                rect = maxCombineRect(rect, br.columnRect)
                if ((rect.left < 0F && br.left) or
                    (rect.right > w && !br.left))
                   continue
                br.paint(canvas)
            }
            lastHeight = if (rect.bottom - lastHeight > basePadding) rect.bottom else lastHeight + brPadding

            val lines: List<PaintLine> = table.exportPaintLines(paintLine.color, paintLine.strokeWidth)
            for (line in lines) {
                line.paint(canvas)
            }
        }
    }

    private fun paintNode(canvas: Canvas, node: TreeNode<String>): RectF {
        textPaint.textSize = nodeStyle.fontSize
        val h = nodePadding+nodeHeight
        val w = (canvas.width-nodeStyle.width)/2
        val startHeight = lastHeight
        val rect = RectF(w, startHeight, w+nodeStyle.width, startHeight+nodeHeight)
        for (value in node.values) {
            canvas.drawRoundRect(rect, nodeStyle.rx, nodeStyle.ry, nodePaint)
            canvas.drawCenterText(value, rect, textPaint)
            rect downOn h
        }
        lastHeight = rect.top
        rect.top = startHeight
        rect.bottom = lastHeight
        return rect
    }

    /*
    Create new rect from maximum width and height of both rects
     */
    private fun maxCombineRect(a: RectF, b: RectF): RectF {
        val top: Float = if (a.top < b.top) a.top else b.top
        val bottom: Float = if (a.bottom > b.bottom) a.bottom else b.bottom
        val right: Float = if (a.right > b.right) a.right else b.right
        val left: Float = if (a.left < b.left) a.left else b.left
        return RectF(left, top, right, bottom)
    }
}

fun Tree<String>.paintTree(padding: Float, @ColorInt textColor: Int, paintNode: NodeStyle, vararg paintBranch: BranchStyle)
: PaintTree = PaintTree(this, TreeStyle(padding, textColor, paintNode, paintBranch.toMutableList()))