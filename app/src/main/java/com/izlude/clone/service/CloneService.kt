package com.izlude.clone.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.izlude.clone.DualSpaceApp
import com.izlude.clone.R
import com.izlude.clone.core.CloneManager
import kotlinx.coroutines.*

/**
 * Foreground service for handling clone operations
 */
class CloneService : Service() {

    companion object {
        const val ACTION_CLONE = "com.izlude.clone.ACTION_CLONE"
        const val ACTION_REMOVE = "com.izlude.clone.ACTION_REMOVE"
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_APP_NAME = "extra_app_name"
        private const val NOTIFICATION_ID = 1001
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var cloneManager: CloneManager

    override fun onCreate() {
        super.onCreate()
        cloneManager = CloneManager.getInstance(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val packageName = intent?.getStringExtra(EXTRA_PACKAGE_NAME)
        val appName = intent?.getStringExtra(EXTRA_APP_NAME) ?: packageName ?: ""

        if (packageName == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIFICATION_ID, createNotification(appName))

        serviceScope.launch {
            when (action) {
                ACTION_CLONE -> {
                    cloneManager.cloneApp(packageName, appName)
                    // Broadcast result
                    sendBroadcast(Intent("com.izlude.clone.CLONE_COMPLETE").apply {
                        putExtra(EXTRA_PACKAGE_NAME, packageName)
                    })
                }
                ACTION_REMOVE -> {
                    cloneManager.removeClone(packageName)
                    sendBroadcast(Intent("com.izlude.clone.CLONE_REMOVED").apply {
                        putExtra(EXTRA_PACKAGE_NAME, packageName)
                    })
                }
            }
            delay(500) // Small delay for notification visibility
            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun createNotification(appName: String): Notification {
        return NotificationCompat.Builder(this, DualSpaceApp.CHANNEL_ID)
            .setContentTitle(getString(R.string.cloning_in_progress))
            .setContentText(appName)
            .setSmallIcon(R.drawable.ic_clone)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setProgress(0, 0, true)
            .build()
    }
}
