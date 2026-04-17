package com.lebedaliv2601.base.foundation.string

fun <C : CharSequence> C.ifEmptyNull(): C? = ifEmpty { null }

fun String?.ifNull(other: String): String {
    return this ?: other
}

fun String?.toIntOrZero(): Int = this?.toIntOrNull() ?: 0

fun String?.toLongOrZero(): Long = this?.toLongOrNull() ?: 0L

fun String?.toFloatOrZero(): Float = this?.toFloatOrNull() ?: 0f

fun String?.toDoubleOrZero(): Double = this?.toDoubleOrNull() ?: 0.0

fun Any?.toStringOrEmpty() = this?.toString().orEmpty()