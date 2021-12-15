package com.hugegreenbug.launchy

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ComponentInfo
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
        var list = packageManager.queryIntentActivities(intent, MATCH_ALL)
            .map { it.activityInfo }
            .map { App(it.packageName, it.loadLabel(packageManager).toString(), it, false) }
        val listIter = list.iterator()
        while (listIter.hasNext()) {
            val app = listIter.next()
            val drawable = getAppIcon(app.componentInfo)
            if (drawable == null) {
                list = list.filterIndexed { _, element ->
                    element.packageName != app.packageName
                }
            }
        }

        return list
    }

    fun startApp(app: App) {
        val intent = packageManager.getLeanbackLaunchIntentForPackage(app.packageName)
        context.startActivity(intent)
    }

    fun getAppIcon(componentInfo: ComponentInfo): Drawable?
            = appIconCache[componentInfo.packageName] ?: loadAppIcon(componentInfo)

    private fun loadAppIcon(componentInfo: ComponentInfo): Drawable? {
        return try {
            val drawable = componentInfo.loadBanner(packageManager) ?: return null

            drawable
        } catch (e: SecurityException) {
            null
        }
    }
}