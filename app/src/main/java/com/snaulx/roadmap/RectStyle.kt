package com.snaulx.roadmap

import androidx.annotation.ColorInt

data class RectStyle(@ColorInt val color: Int, val fontSize: Float, val width: Float, val height: Float,
                     val rx: Float, val ry: Float)

data class TreeStyle(val padding: Float, @ColorInt val lineColor: Int, val lineWidth: Float, @ColorInt val textColor: Int,
                    val nodeStyle: NodeStyle, val branchStyles: MutableList<BranchStyle> = mutableListOf())

data class NodeStyle(val nodePadding: Float, val style: RectStyle, val branchPadding: Float)

data class BranchStyle(val valuesPadding: Float, val style: RectStyle, val childrenPadding: Float)


