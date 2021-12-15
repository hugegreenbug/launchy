package com.hugegreenbug.launchy

import android.content.pm.ComponentInfo
import android.graphics.Bitmap

data class App(val packageName: String, val label: String,
               val componentInfo: ComponentInfo, var favorite: Boolean)
data class Unsplash(val author: String, val url: String, val thumb: Bitmap, val name: String)
