package com.snaulx.roadmap

import android.graphics.PointF
import android.graphics.RectF
import androidx.annotation.ColorInt

class BranchTable(node: TreeNode<String>, private val styles: List<BranchStyle>) {

    private val leftTable: MutableList<MutableList<List<String>>> = mutableListOf()
    private val rightTable: MutableList<MutableList<List<String>>> = mutableListOf()

    init {
        var left = true
        for (branch in node.branches) {
            parseBranch(branch, left)
            left = !left
        }
    }

    private fun parseBranch(branch: TreeBranch<String>, left: Boolean, index: Int = 0) {
        addBranchValues(branch.values, left, index)
        if (branch.hasChildren) {
            for (child in branch.children) {
                parseBranch(child, left, index + 1)
            }
        }
    }

    private fun addBranchValues(values: List<String>, left: Boolean, index: Int) {
        try {
            if (left) leftTable[index].add(values)
            else rightTable[index].add(values)
        } catch (e: IndexOutOfBoundsException) {
            if (left) leftTable.add(index, mutableListOf())
            else rightTable.add(index, mutableListOf())
            addBranchValues(values, left, index)
        }
    }

    fun exportPaintBranches(nodeRect: RectF, @ColorInt textColor: Int): List<PaintBranch> {
        val branches = mutableListOf<PaintBranch>()
        val offset = PointF(nodeRect.left, nodeRect.top)
        for (i in leftTable.indices) {
            val paintBranch = PaintBranch(styles[i], textColor, offset, leftTable[i].toList(), left = true)
            branches.add(paintBranch)
            offset.x -= paintBranch.columnRect.left + styles[i].childrenPadding
        }
        offset.set(nodeRect.right, nodeRect.top)
        for (i in rightTable.indices) {
            val paintBranch = PaintBranch(styles[i], textColor, offset, rightTable[i].toList(), left = false)
            branches.add(paintBranch)
            offset.x += paintBranch.columnRect.right + styles[i].childrenPadding
        }
        return branches.toList()
    }
}
