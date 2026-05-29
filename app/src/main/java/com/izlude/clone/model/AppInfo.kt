package com.izlude.clone.model

import android.graphics.drawable.Drawable

/**
 * Represents an installed application on the device
 */
data class AppInfo(
    val appName: String,
    val packageName: String,
    val icon: Drawable?,
    val versionName: String = "",
    val appSize: Long = 0L,
    val isSystemApp: Boolean = false,
    val isCloned: Boolean = false,
    val installTime: Long = 0L
) {
    /**
     * Returns a human-readable size string
     */
    fun getFormattedSize(): String {
        if (appSize <= 0) return "--"
        val kb = appSize / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> String.format("%.1f GB", gb)
            mb >= 1.0 -> String.format("%.1f MB", mb)
            kb >= 1.0 -> String.format("%.0f KB", kb)
            else -> "$appSize B"
        }
    }
}
