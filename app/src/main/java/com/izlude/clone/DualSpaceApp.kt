package com.izlude.clone

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class DualSpaceApp : Application() {

    companion object {
        const val CHANNEL_ID = "dual_space_clone_service"
        const val PREFS_NAME = "dual_space_prefs"
        const val KEY_CLONED_APPS = "cloned_apps"

        lateinit var instance: DualSpaceApp
            private set
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        try {
            top.niunaijun.blackbox.BlackBoxCore.get().doAttachBaseContext(base, object : top.niunaijun.blackbox.app.configuration.ClientConfiguration() {
                override fun getHostPackageName(): String {
                    return base.packageName
                }
                override fun isHideRoot(): Boolean = false
                override fun isHideXposed(): Boolean = false
                override fun isEnableDaemonService(): Boolean = false
                override fun requestInstallPackage(file: java.io.File?, userId: Int): Boolean = false
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        try {
            top.niunaijun.blackbox.BlackBoxCore.get().doCreate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_desc)
                setShowBadge(false)
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
