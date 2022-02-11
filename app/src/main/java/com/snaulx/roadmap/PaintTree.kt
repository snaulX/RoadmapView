package com.snaulx.roadmap

import android.graphics.*
import androidx.annotation.ColorInt
import kotlin.math.max

data class PaintTree(val tree: Tree<String>, val style: TreeStyle) {

    data class PaintBranch(val style: BranchStyle, @ColorInt val textColor: Int,
                           val offset: RectF, val branches: List<List<String>>) {
        private val paint = Paint()
        private val textPaint = Paint()
        private val rects: List<List<RectF>>

        private val rx: Float
        private val ry: Float

        init {
            paint.style = Paint.Style.FILL
            paint.color = style.style.color
            textPaint.textSize = style.style.fontSize
            textPaint.color = textColor
            val rectStyle: RectStyle = style.style
            rx = rectStyle.rx
            ry = rectStyle.ry

            val valHeight: Float = rectStyle.height + style.valuesPadding
            val mutRects = mutableListOf<List<RectF>>()
            var left = true
            val rect = RectF(0F, 0F, rectStyle.height, rectStyle.width)
            for (branch in branches) {
                val branchList = mutableListOf<RectF>()
                for (i in branch.indices) {
                    branchList.add(rect)
                    rect downOn valHeight
                }
                mutRects.add(branchList.toList())
                rect downOn style.childrenPadding - style.valuesPadding
                left = !left
            }
            rects = mutRects.toList()
        }

        fun paint(canvas: Canvas) {
            for (i in branches.indices) {
                val values: List<String> = branches[i]
                val valRects: List<RectF> = rects[i]
                for (j in values.indices) {
                    val rect = valRects[j]
                    canvas.drawRoundRect(rect, rx, ry, paint)
                    canvas.drawCenterText(values[j], rect, textPaint)
                }
            }
        }
    }

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
    // You can calculate all in constructor and cache it
    // Like in PaintBranch class
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
                    rect downOn brHeight
                }

                //if (branch.hasChildren) branchLeft = !branchLeft
                branchLeft = !branchLeft
            }
        }
    }

    var lastHeight = 0F

    private fun paintNode(canvas: Canvas, node: TreeNode<String>): RectF {
        textPaint.textSize = nodeStyle.fontSize
        val h = nodePadding+nodeHeight
        val w = (canvas.width-nodeStyle.width)/2
        val startHeight = lastHeight+basePadding
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
    fun paintMainBranch(canvas: Canvas, nodeRect: RectF): RectF {
        val rect = RectF()

        return rect
    }
    fun paintBranch(canvas: Canvas, index: Int = 0): RectF {
        val rect = RectF()
        val style: BranchStyle = branchStyles[index]

        return rect
    }

    /*
    Create new rect from maximum width and height of both rects
     */
    private fun maxCombineRect(a: RectF, b: RectF): RectF {
        val top: Float = if (a.top > b.top) a.top else b.top
        val bottom: Float = if (a.bottom > b.bottom) a.bottom else b.bottom
        val right: Float = if (a.right > b.right) a.right else b.right
        val left: Float = if (a.left > b.left) a.left else b.left
        return RectF(left, top, right, bottom)
    }
}

data class PaintBranch(val branch: TreeBranch<String>, val styles: List<BranchStyle> = listOf()) {
    val width: Float = calcMaxWidth()
    val height: Float = calcMaxHeight()

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