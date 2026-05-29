package com.dualspace.clone.util

import android.content.Context
import android.content.SharedPreferences
import com.dualspace.clone.DualSpaceApp
import com.dualspace.clone.model.ClonedApp

/**
 * Manages persisted cloned app data using SharedPreferences
 */
object CloneStorage {

    private fun getPrefs(): SharedPreferences {
        return DualSpaceApp.instance.getSharedPreferences(
            DualSpaceApp.PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    /**
     * Save a cloned app entry
     */
    fun addClonedApp(packageName: String, appName: String) {
        val prefs = getPrefs()
        val clonedSet = getClonedPackages().toMutableSet()
        clonedSet.add(packageName)
        prefs.edit()
            .putStringSet(DualSpaceApp.KEY_CLONED_APPS, clonedSet)
            .putString("clone_name_$packageName", appName)
            .putLong("clone_time_$packageName", System.currentTimeMillis())
            .apply()
    }

    /**
     * Remove a cloned app entry
     */
    fun removeClonedApp(packageName: String) {
        val prefs = getPrefs()
        val clonedSet = getClonedPackages().toMutableSet()
        clonedSet.remove(packageName)
        prefs.edit()
            .putStringSet(DualSpaceApp.KEY_CLONED_APPS, clonedSet)
            .remove("clone_name_$packageName")
            .remove("clone_time_$packageName")
            .apply()
    }

    /**
     * Get all cloned package names
     */
    fun getClonedPackages(): Set<String> {
        return getPrefs().getStringSet(DualSpaceApp.KEY_CLONED_APPS, emptySet()) ?: emptySet()
    }

    /**
     * Check if a package is cloned
     */
    fun isCloned(packageName: String): Boolean {
        return getClonedPackages().contains(packageName)
    }

    /**
     * Get all cloned apps as ClonedApp objects
     */
    fun getAllClonedApps(): List<ClonedApp> {
        val prefs = getPrefs()
        return getClonedPackages().map { packageName ->
            ClonedApp(
                appName = prefs.getString("clone_name_$packageName", packageName) ?: packageName,
                packageName = packageName,
                clonedAt = prefs.getLong("clone_time_$packageName", 0L)
            )
        }.sortedByDescending { it.clonedAt }
    }

    /**
     * Get cloned apps count
     */
    fun getClonedCount(): Int {
        return getClonedPackages().size
    }
}
