package com.snaulx.roadmap

import java.util.*

data class Tree<T>(val body: LinkedList<TreeNode<T>> = LinkedList()): Iterable<TreeNode<T>> {
    override fun iterator(): Iterator<TreeNode<T>> = body.iterator()

    fun node(vararg values: T, func: TreeNode<T>.() -> Unit = {}) = body.add(TreeNode(values.toList()).apply(func))
}