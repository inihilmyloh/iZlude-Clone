package com.dualspace.clone.model

/**
 * Represents a cloned application in the DualSpace
 */
data class ClonedApp(
    val appName: String,
    val packageName: String,
    val clonedAt: Long = System.currentTimeMillis(),
    val isRunning: Boolean = false
)
