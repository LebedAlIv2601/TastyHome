package com.tastyhome.core.themeManager.api

enum class AppTheme(val value: String) {
    Light("light"),
    Dark("dark"),
    System("system");

    companion object {
        fun fromValue(value: String): AppTheme {
            return entries.firstOrNull { it.value == value } ?: System
        }
    }
}