package com.example

import kotlin.streams.toList

interface FCC<T>: Iterable<T> {
    val list: List<T>

    fun <R> map(transform: (T) -> R): List<R> = list.map(transform)

    override fun iterator(): Iterator<T> = list.iterator()

    fun <R> pmap(transform: (T) -> R): List<R> = list.parallelStream().map(transform).toList()

    fun isEmpty() = list.isEmpty()

    fun isNotEmpty() = !isEmpty()
}