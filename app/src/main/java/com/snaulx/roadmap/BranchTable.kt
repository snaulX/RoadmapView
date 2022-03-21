package com.snaulx.roadmap

import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import androidx.annotation.ColorInt

internal class BranchTable(node: TreeNode<String>, private val styles: List<BranchStyle>) {

    private val leftTable: MutableList<MutableList<TreeBranch<String>>> = mutableListOf()
    private val rightTable: MutableList<MutableList<TreeBranch<String>>> = mutableListOf()

    // Filled in exportPaintBranches function
    private val paintLines = mutableListOf<Pair<PaintBranch, PaintBranch>>()

    init {
        var left = true
        for (branch in node.branches) {
            parseBranch(branch, left)
            left = !left
        }
    }

    private fun parseBranch(branch: TreeBranch<String>, left: Boolean, index: Int = 0) {
        addBranchValues(branch, left, index)
        if (branch.hasChildren) {
            for (child in branch.children) {
                parseBranch(child, left, index + 1)
            }
        }
    }

    private fun addBranchValues(branch: TreeBranch<String>, left: Boolean, index: Int) {
        try {
            if (left) leftTable[index].add(branch)
            else rightTable[index].add(branch)
        } catch (e: IndexOutOfBoundsException) {
            if (left) leftTable.add(index, mutableListOf())
            else rightTable.add(index, mutableListOf())
            addBranchValues(branch, left, index)
        }
    }

    fun exportPaintBranches(nodeRect: RectF, @ColorInt textColor: Int): List<PaintBranch> {
        val branches = mutableListOf<PaintBranch>()
        val offset = PointF(nodeRect.left, nodeRect.top)
        var prevBranch: PaintBranch? = null
        for (i in leftTable.indices) {
            val paintBranch = PaintBranch(styles[i], textColor, offset, leftTable[i].toList(), left = true)
            if (prevBranch != null) {
                paintLines.add(prevBranch to paintBranch)
            }
            prevBranch = paintBranch
            branches.add(paintBranch)
            offset.x = paintBranch.columnRect.left - styles[i].childrenPadding
        }
        prevBranch = null
        offset.set(nodeRect.right, nodeRect.top)
        for (i in rightTable.indices) {
            val paintBranch = PaintBranch(styles[i], textColor, offset, rightTable[i].toList(), left = false)
            if (prevBranch != null) {
                paintLines.add(prevBranch to paintBranch)
            }
            prevBranch = paintBranch
            branches.add(paintBranch)
            offset.x = paintBranch.columnRect.right + styles[i].childrenPadding
        }
        return branches.toList()
    }

    // exportPaintBranches *must be called before* this call
    fun exportPaintLines(@ColorInt lineColor: Int, lineWidth: Float): List<PaintLine> {
        val mutLines = mutableListOf<PaintLine>()
        val paint = Paint().apply {
            strokeWidth = lineWidth
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = lineColor
        }
        for (line in paintLines) {
            mutLines.add(PaintLine(line.first, line.second, paint))
        }
        return mutLines.toList()
    }
}
