package com.snaulx.roadmap

fun <T> tree(func: Tree<T>.() -> Unit = {}): Tree<T> = Tree<T>().apply(func)