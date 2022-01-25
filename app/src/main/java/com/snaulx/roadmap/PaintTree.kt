package com.snaulx.roadmap

import android.graphics.*
import android.text.TextPaint
import androidx.annotation.ColorInt
import androidx.core.graphics.toRect

data class PaintTree(val tree: Tree<String>, val style: TreeStyle) {

    private val basePadding = style.padding

    private val nodeStyle: RectStyle = style.nodeStyle.style
    private val nodeHeight: Float = nodeStyle.height
    private val nodePadding: Float = style.nodeStyle.nodePadding
    private val nodePaint = Paint()

    private val branchStyles: List<BranchStyle> = style.branchStyles.toList()
    private val branchPainting = Paint() // we need to change it every branch paint

    private val textPaint = Paint()

    init {
        nodePaint.style = Paint.Style.FILL
        nodePaint.color = nodeStyle.color
        textPaint.textSize = nodeStyle.fontSize
        textPaint.color = style.textColor
    }

    // !!! YOU CAN OPTIMISE IT !!!
    // You can cache all calculations
    fun paint(canvas: Canvas) {
        val paintLine = Paint() // it should be as private member of class
        paintLine.color = Color.rgb(250, 174, 53)
        paintLine.strokeWidth = 7F
        for (node in tree.body) {

            val rect = paintNode(canvas, node)
            canvas.drawLine(rect.centerX(), lastHeight, rect.centerX(), lastHeight+basePadding, paintLine)

            val brPadding = style.nodeStyle.branchPadding
            var branchLeft = true
            for (j in node.branches.indices) {
                val branch = node.branches[j]

                val brStyle = branchStyles[j].style
                branchPainting.color = brStyle.color
                textPaint.textSize = brStyle.fontSize

                val brHeight = branchStyles[j].valuesPadding
                if (branchLeft) {
                    val r = rect.left - brPadding
                    rect.set(r, basePadding, r - brStyle.width, basePadding+brStyle.height)
                } else {
                    val r = rect.right + brPadding
                    rect.set(r, basePadding, r + brStyle.width, basePadding+brStyle.height)
                }

                for (value in branch.values) {
                    canvas.drawRoundRect(rect, brStyle.rx, brStyle.ry, branchPainting)
                    canvas.drawCenterText(value, rect, textPaint)
                    rect.bottom += brHeight
                    rect.top += brHeight
                }

                //if (branch.hasChildren) branchLeft = !branchLeft
                branchLeft = !branchLeft
            }
        }
    }

    var lastHeight = 0F

    fun paintNode(canvas: Canvas, node: TreeNode<String>): RectF {
        textPaint.textSize = nodeStyle.fontSize
        val h = nodePadding+nodeHeight
        val w = (canvas.width-nodeStyle.width)/2
        val startHeight = lastHeight+basePadding
        val rect = RectF(w, startHeight, w+nodeStyle.width, startHeight+nodeHeight)
        for (value in node.values) {
            canvas.drawRoundRect(rect, nodeStyle.rx, nodeStyle.ry, nodePaint)
            canvas.drawCenterText(value, rect, textPaint)
            rect.bottom += h
            rect.top += h
        }
        lastHeight = rect.top
        rect.top = startHeight
        rect.bottom = lastHeight
        return rect
    }
    fun paintBranch(canvas: Canvas): RectF {
        val rect = RectF()
        
        return rect
    }
}

data class PaintBranch(val branch: TreeBranch<String>, val styles: List<BranchStyle> = listOf()) {
    val width: Float = calcMaxWidth()
    val height: Float = calcMaxHeight()

    //private val brPadding = style.nodeStyle.branchPadding
    private var branchLeft = true

    /*fun paint(canvas: Canvas, left: Boolean, width: Float, rect: RectF) {

        val brStyle = styles[0].style
        branchPainting.color = brStyle.color
        textPaint.textSize = brStyle.fontSize

        val brHeight = styles[j].valuesPadding
        if (branchLeft) {
            val r = rect.left - brPadding
            rect.set(r, basePadding, r - brStyle.width, basePadding+brStyle.height)
        } else {
            val r = rect.right + brPadding
            rect.set(r, basePadding, r + brStyle.width, basePadding+brStyle.height)
        }

        for (value in branch.values) {
            canvas.drawRoundRect(rect, brStyle.rx, brStyle.ry, branchPainting)
            canvas.drawCenterText(value, rect, textPaint)
            rect.bottom += h
            rect.top += h
        }

        //if (branch.hasChildren) branchLeft = !branchLeft
    }

    private fun childPaint(index: Int) {
    }*/

    private fun calcMaxHeight(index: Int = 0, maxHeight: Float = 0F): Float {
        var branchHeight = 0F
        val br = if (index == 0) branch else branch.children[index-1]
        val valCount = br.values.size
        val curStyle = styles[index]
        branchHeight += valCount * curStyle.style.height
        branchHeight += (valCount-1) * curStyle.valuesPadding
        val h = if (maxHeight > branchHeight) maxHeight else branchHeight
        return if (br.hasChildren) {
            val childCount = br.children.size
            calcMaxHeight(index + 1, h) * childCount + curStyle.childrenPadding * (childCount-1)
        } else {
            h
        }
    }

    private fun calcMaxWidth(index: Int = 0, width: Float = 0F): Float {
        val curStyle = styles[index]
        val w = curStyle.style.width + width
        val hasChildren = if (index == 0) branch.hasChildren else branch.children[index-1].hasChildren
        return if (hasChildren) {
            calcMaxWidth(index + 1, w + curStyle.childrenPadding*2)
        } else {
            w
        }
    }
}

fun Tree<String>.paintTree(padding: Float, @ColorInt textColor: Int, paintNode: NodeStyle, vararg paintBranch: BranchStyle)
: PaintTree = PaintTree(this, TreeStyle(padding, textColor, paintNode, paintBranch.toMutableList()))

//fun TreeBranch<String>.paintBranch(func: PaintBranch.() -> Unit): PaintBranch = PaintBranch(this).apply(func)