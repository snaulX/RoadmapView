package com.snaulx.roadmap

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.ColorInt

internal class PaintNode(node: TreeNode<String>, @ColorInt textColor: Int, style: NodeStyle, startHeight: Float,
                         canvasWidth: Int, branchStyles: List<BranchStyle>, @ColorInt lineColor: Int, lineWidth: Float) {

    val nodeRect: RectF
    val rect: RectF

    private val valuesRects: List<RectF>
    private val values: List<String> = node.values

    private val rectStyle: RectStyle = style.style

    private val paintBranches: List<PaintBranch>
    private val paintLines: List<PaintLine>

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
        val mutRect = RectF(w, startHeight, w+rectStyle.width, startHeight+h)
        val mutRects = mutableListOf<RectF>()

        for (value in node.values) {
            mutRects.add(mutRect.clone())
            mutRect downOn h+style.nodePadding
        }
        nodeRect = RectF(mutRect.left, startHeight, mutRect.right, mutRect.bottom)
        rect = nodeRect.clone()
        valuesRects = mutRects.toList()

        val table = BranchTable(node, branchStyles)
        val baseRect = nodeRect.clone()
        baseRect.left -= style.branchPadding
        baseRect.right += style.branchPadding
        paintBranches = table.exportPaintBranches(baseRect, textColor)
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
        for (br in drawableBranches) {
            br.paint(canvas)
        }
        for (line in paintLines) {
            line.paint(canvas)
        }
    }
}
