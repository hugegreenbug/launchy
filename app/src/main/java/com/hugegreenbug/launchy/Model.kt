package com.hugegreenbug.launchy

import android.content.pm.PackageItemInfo
import android.graphics.Bitmap

data class App(val packageName: String, val label: String,
               val packageItemInfo: PackageItemInfo, var favorite: Boolean)
data class Unsplash(val author: String, val url: String, val thumb: Bitmap, val name: String)
