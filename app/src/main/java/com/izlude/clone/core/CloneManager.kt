package com.izlude.clone.core

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.UserManager
import com.izlude.clone.receiver.DualSpaceDeviceAdminReceiver
import com.izlude.clone.util.CloneStorage

/**
 * Core manager for handling app cloning via Android Work Profile.
 *
 * This uses the Android Managed Profile (Work Profile) mechanism,
 * which is the same approach used by legitimate apps like Shelter and Island.
 * It creates a separate user profile where apps can be installed independently,
 * effectively allowing "dual" instances of the same app.
 */
class CloneManager(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: CloneManager? = null

        fun getInstance(context: Context): CloneManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CloneManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    /**
     * Virtual Engine is always ready
     */
    fun isWorkProfileSetup(): Boolean {
        return true
    }

    fun canProvisionManagedProfile(): Boolean {
        return true
    }

    /**
     * Clone an app by installing it into BlackBox Virtual Engine
     */
    fun cloneApp(packageName: String, appName: String): CloneResult {
        return try {
            // Check if already cloned
            if (CloneStorage.isCloned(packageName)) {
                return CloneResult.AlreadyCloned
            }

            // Install package in Virtual Engine (User 0)
            val installResult = top.niunaijun.blackbox.BlackBoxCore.get().installPackageAsUser(packageName, 0)
            if (installResult.success) {
                // Save to local storage
                CloneStorage.addClonedApp(packageName, appName)
                CloneResult.Success
            } else {
                CloneResult.Error("Gagal kloning: ${installResult.msg}")
            }
        } catch (e: Exception) {
            CloneResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Remove a cloned app
     */
    fun removeClone(packageName: String): Boolean {
        return try {
            top.niunaijun.blackbox.BlackBoxCore.get().uninstallPackageAsUser(packageName, 0)
            CloneStorage.removeClonedApp(packageName)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Launch a cloned app
     * Attempts to launch the app inside BlackBox
     */
    fun launchClonedApp(packageName: String): Boolean {
        return try {
            top.niunaijun.blackbox.BlackBoxCore.get().launchApk(packageName, 0)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Result of a clone operation
     */
    sealed class CloneResult {
        data object Success : CloneResult()
        data object AlreadyCloned : CloneResult()
        data object NeedsProvisioning : CloneResult()
        data class Error(val message: String) : CloneResult()
    }
}
