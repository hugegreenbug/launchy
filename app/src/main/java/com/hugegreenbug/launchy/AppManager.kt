package com.hugegreenbug.launchy

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageItemInfo
import android.content.pm.PackageManager.MATCH_ALL
import android.graphics.drawable.Drawable
import android.util.ArrayMap

class AppManager(private val context: Context) {
    private val cachesize = 400
    private val appIconCache = ArrayMap<String, Drawable>(cachesize)
    private val packageManager = context.packageManager

    @SuppressLint("QueryPermissionsNeeded")
    fun getLaunchableApps(): List<App> {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)

        val list = ( getAppsThroughPackages() + getAppsThroughIntent(intent) )
            .distinctBy { it.packageName }
            .sortedBy { it.label }

        return list
    }

    private fun getAppsThroughIntent(intent: Intent) =
        packageManager.queryIntentActivities(intent, MATCH_ALL)
            .map { it.activityInfo }
            .map { App(it.packageName, it.loadLabel(packageManager).toString(), it, false) }

    private fun getAppsThroughPackages() = packageManager.getInstalledPackages(0)
        .filter { (it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
        .map { it.applicationInfo }
        .map { App(it.packageName, it.loadLabel(packageManager).toString(), it, false) }

    fun startApp(app: App) {
        val intent = packageManager.getLeanbackLaunchIntentForPackage(app.packageName) ?:
            packageManager.getLaunchIntentForPackage(app.packageName)
        context.startActivity(intent)
    }

    fun getAppIcon(info: PackageItemInfo): Drawable
            = appIconCache[info.packageName] ?: loadAppIcon(info)

    private fun loadAppIcon(info: PackageItemInfo): Drawable {
        return info.loadBanner(packageManager) ?: info.loadIcon(packageManager)
    }
}