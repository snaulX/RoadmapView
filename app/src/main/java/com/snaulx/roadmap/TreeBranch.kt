package com.snaulx.roadmap

data class TreeBranch<T>(val values: List<T> = listOf(), val parent: TreeBranch<T>? = null,
                         val children: MutableList<TreeBranch<T>> = mutableListOf()): Iterable<TreeBranch<T>> {
    val hasChildren: Boolean get() = children.isEmpty()

    fun branch(vararg values: T, func: TreeBranch<T>.() -> Unit = {}) {
        children.add(TreeBranch(values.toList(), this).apply(func))
    }

    override fun iterator(): Iterator<TreeBranch<T>> = children.iterator()

    fun get(index: Int): T = values[index]
}