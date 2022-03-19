package com.snaulx.roadmap

import android.graphics.*
import androidx.annotation.ColorInt

data class PaintTree(val tree: Tree<String>, val style: TreeStyle) {

    private var paintNodes = listOf<PaintNode>()
    private var isNodesGenerated = false

    private fun generateNodes(canvasWidth: Int) {
        val padding = style.padding
        var startHeight = 0F
        val mutNodes = mutableListOf<PaintNode>()

        for (node in tree.body) {
            val paintNode = PaintNode(node, style.textColor, style.nodeStyle, startHeight, canvasWidth,
                style.branchStyles.toList(), style.lineColor, style.lineWidth)
            mutNodes.add(paintNode)
            val h = paintNode.rect.bottom
            startHeight = if (h - paintNode.nodeRect.bottom > padding) h else startHeight + padding
        }
        paintNodes = mutNodes.toList()
        isNodesGenerated = true
    }

    fun move(dirX: Float, dirY: Float) {
        for (node in paintNodes) {
            node.move(dirX, dirY)
        }
    }

    fun paint(canvas: Canvas) {
        if (!isNodesGenerated) generateNodes(canvas.width)

        for (node in paintNodes) {
            node.paint(canvas)
        }
    }
}

fun Tree<String>.paintTree(padding: Float, @ColorInt lineColor: Int, lineWidth: Float, @ColorInt textColor: Int,
                           paintNode: NodeStyle, vararg paintBranch: BranchStyle): PaintTree
= PaintTree(this, TreeStyle(padding, lineColor, lineWidth, textColor, paintNode, paintBranch.toMutableList()))