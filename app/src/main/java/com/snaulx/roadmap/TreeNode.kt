package com.snaulx.roadmap

data class TreeNode<T>(val values: List<T> = listOf(),
                       val branches: MutableList<TreeBranch<T>> = mutableListOf()): Iterable<TreeBranch<T>> {
    override fun iterator(): Iterator<TreeBranch<T>> = branches.iterator()

    inline fun branch(vararg values: T, func: TreeBranch<T>.() -> Unit = {}) {
        branches.add(TreeBranch(values.toList()).apply(func))
    }
}
