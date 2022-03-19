package com.snaulx.roadmap

import android.graphics.*
import androidx.annotation.ColorInt

data class PaintTree(val tree: Tree<String>, val style: TreeStyle) {

    private val linePaint = Paint()
    private var lines = listOf<Pair<PointF, PointF>>()
    private var paintNodes = listOf<PaintNode>()
    private var isNodesGenerated = false

    init {
        linePaint.color = style.lineColor
        linePaint.style = Paint.Style.STROKE
        linePaint.isAntiAlias = true
        linePaint.strokeWidth = style.lineWidth
    }

    private fun generateNodes(canvasWidth: Int) {
        val padding = style.padding
        var startHeight = 0F
        val mutNodes = mutableListOf<PaintNode>()
        val mutLines = mutableListOf<Pair<PointF, PointF>>()
        val pointX = (canvasWidth/2).toFloat()
        val startPoint = PointF(pointX, 0F)
        val endPoint = PointF(pointX, 0F)

        for (node in tree.body) {
            val paintNode = PaintNode(node, style.textColor, style.nodeStyle, startHeight, canvasWidth,
                style.branchStyles.toList(), style.lineColor, style.lineWidth)
            mutNodes.add(paintNode)
            endPoint.y = paintNode.nodeRect.top
            mutLines.add(startPoint.clone() to endPoint.clone())
            startPoint.y = paintNode.nodeRect.bottom
            val h = paintNode.rect.bottom
            startHeight = if (h - paintNode.nodeRect.bottom > padding) h else startHeight + padding
        }
        paintNodes = mutNodes.toList()
        lines = mutLines.toList()
        isNodesGenerated = true
    }

    fun move(dirX: Float, dirY: Float) {
        for (line in lines) {
            line.first leftOn dirX
            line.first upOn dirY
            line.second leftOn dirX
            line.second upOn dirY
        }
        for (node in paintNodes) {
            node.move(dirX, dirY)
        }
    }

    fun paint(canvas: Canvas) {
        if (!isNodesGenerated) generateNodes(canvas.width)

        for (node in paintNodes) {
            node.paint(canvas)
        }
        for (line in lines) {
            canvas.drawLine(line.first, line.second, linePaint)
        }
    }
}

fun Tree<String>.paintTree(padding: Float, @ColorInt lineColor: Int, lineWidth: Float, @ColorInt textColor: Int,
                           paintNode: NodeStyle, vararg paintBranch: BranchStyle): PaintTree
= PaintTree(this, TreeStyle(padding, lineColor, lineWidth, textColor, paintNode, paintBranch.toMutableList()))