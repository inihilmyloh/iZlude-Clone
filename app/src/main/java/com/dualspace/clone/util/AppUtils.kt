package com.dualspace.clone.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.dualspace.clone.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utility class for loading and filtering installed applications
 */
object AppUtils {

    // Popular apps that are commonly cloned
    private val POPULAR_PACKAGES = setOf(
        "com.whatsapp",
        "com.instagram.android",
        "com.facebook.katana",
        "com.facebook.lite",
        "com.twitter.android",
        "com.zhiliaoapp.musically", // TikTok
        "com.snapchat.android",
        "org.telegram.messenger",
        "com.discord",
        "com.spotify.music",
        "com.google.android.youtube",
        "com.shopee.id",
        "com.tokopedia.tkpd",
        "id.dana",
        "com.gojek.app",
        "com.grabtaxi.passenger",
        "com.bukalapak.android",
        "com.lazada.android",
        "com.tencent.mm", // WeChat
        "com.tencent.ig", // PUBG Mobile
        "com.supercell.clashofclans",
        "com.mobile.legends",
        "com.garena.game.fctrl", // Free Fire
        "jp.naver.line.android", // LINE
        "com.linkedin.android",
        "com.pinterest",
        "com.reddit.frontpage"
    )

    /**
     * Load all user-installed applications
     */
    suspend fun loadInstalledApps(context: Context): List<AppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getInstalledApplications(
                PackageManager.ApplicationInfoFlags.of(0)
            )
        } else {
            @Suppress("DEPRECATION")
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
        }

        val clonedPackages = CloneStorage.getClonedPackages()

        packages
            .filter { appInfo ->
                // Filter out system apps and our own app
                (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 &&
                        appInfo.packageName != context.packageName
            }
            .map { appInfo ->
                val appName = pm.getApplicationLabel(appInfo).toString()
                val versionName = try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        pm.getPackageInfo(
                            appInfo.packageName,
                            PackageManager.PackageInfoFlags.of(0)
                        ).versionName ?: ""
                    } else {
                        @Suppress("DEPRECATION")
                        pm.getPackageInfo(appInfo.packageName, 0).versionName ?: ""
                    }
                } catch (e: Exception) {
                    ""
                }

                val appSize = try {
                    val sourceDir = appInfo.sourceDir
                    if (sourceDir != null) {
                        java.io.File(sourceDir).length()
                    } else 0L
                } catch (e: Exception) {
                    0L
                }

                AppInfo(
                    appName = appName,
                    packageName = appInfo.packageName,
                    icon = try {
                        pm.getApplicationIcon(appInfo)
                    } catch (e: Exception) {
                        null
                    },
                    versionName = versionName,
                    appSize = appSize,
                    isSystemApp = false,
                    isCloned = clonedPackages.contains(appInfo.packageName)
                )
            }
            .sortedWith(compareByDescending<AppInfo> { isPopular(it.packageName) }
                .thenBy { it.appName.lowercase() })
    }

    /**
     * Check if an app is in the popular list
     */
    fun isPopular(packageName: String): Boolean {
        return POPULAR_PACKAGES.contains(packageName)
    }

    /**
     * Filter apps by search query
     */
    fun filterApps(apps: List<AppInfo>, query: String): List<AppInfo> {
        if (query.isBlank()) return apps
        val lowerQuery = query.lowercase()
        return apps.filter {
            it.appName.lowercase().contains(lowerQuery) ||
                    it.packageName.lowercase().contains(lowerQuery)
        }
    }

    /**
     * Get app icon for a package name
     */
    fun getAppIcon(context: Context, packageName: String): android.graphics.drawable.Drawable? {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get app name for a package name
     */
    fun getAppName(context: Context, packageName: String): String {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
            context.packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }
    }
}
