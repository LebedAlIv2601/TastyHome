package com.lebedaliv2601.base.foundation.collections

inline fun <T> List<T>.mapIf(predicate: (T) -> Boolean, transform: T.() -> T): List<T> {
    return map { item ->
        if (predicate(item)) {
            item.transform()
        } else {
            item
        }
    }
}

fun <T> List<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
    return this.indexOfFirst(predicate).takeIf { it != -1 }
}

fun <T> List<T>.indexOfLastOrNull(predicate: (T) -> Boolean): Int? {
    return this.indexOfLast(predicate).takeIf { it != -1 }
}