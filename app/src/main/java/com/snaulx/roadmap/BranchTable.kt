package com.snaulx.roadmap

import android.graphics.RectF
import androidx.annotation.ColorInt

class BranchTable(node: TreeNode<String>, private val styles: List<BranchStyle>) {
    private val table: MutableList<MutableList<List<String>>> = mutableListOf()

    init {
        for (branch in node.branches) {
            parseBranch(branch)
        }
    }

    private fun parseBranch(branch: TreeBranch<String>, index: Int = 0) {
        addBranchValues(branch.values, index)
        if (branch.hasChildren) {
            for (child in branch.children) {
                parseBranch(child, index + 1)
            }
        }
    }

    private fun addBranchValues(branch: List<String>, index: Int) {
        try {
            table[index].add(branch)
        } catch (e: IndexOutOfBoundsException) {
            table.add(index, mutableListOf())
            addBranchValues(branch, index)
        }
    }

    fun exportPaintBranches(nodeRect: RectF, @ColorInt textColor: Int): List<PaintBranch> {
        val branches = mutableListOf<PaintBranch>()
        val offset: RectF = nodeRect
        for (i in table.indices) {
            val paintBranch = PaintBranch(styles[i], textColor, offset, table[i].toList())
            branches.add(paintBranch)
            offset leftOn paintBranch.columnRect.left + styles[i].childrenPadding
        }
        return branches.toList()
    }
}
