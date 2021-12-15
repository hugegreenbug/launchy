package com.hugegreenbug.launchy

import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.IOException
import android.util.DisplayMetrics
import kotlin.concurrent.thread

class BitmapUtils {
    companion object {
        private fun returnBitmap(displayMetrics: DisplayMetrics, originalImage: Bitmap, width: Int, height: Int): Bitmap? {
            val background = Bitmap.createBitmap(displayMetrics, width, height, Bitmap.Config.ARGB_8888)
            val originalCopy = originalImage.copy(Bitmap.Config.ARGB_8888, true)
            val originalWidth = originalCopy.width.toFloat()
            val originalHeight = originalCopy.height.toFloat()
            val canvas = Canvas(background)
            val scale = width / originalWidth
            val xTranslation = 0.0f
            val yTranslation = (height - originalHeight * scale) / 2.0f
            val transformation = Matrix()
            transformation.postTranslate(xTranslation, yTranslation)
            transformation.preScale(scale, scale)
            val paint = Paint()
            paint.isFilterBitmap = true
            canvas.drawBitmap(originalCopy, transformation, paint)
            return background
        }

        fun toBitmap(drawable:Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }

            val width = if (drawable.bounds.isEmpty) drawable.intrinsicWidth else drawable.bounds.width()
            val height = if (drawable.bounds.isEmpty) drawable.intrinsicHeight else drawable.bounds.height()

            return Bitmap.createBitmap(width.nonZero(), height.nonZero(), Bitmap.Config.ARGB_8888)
                .also {
                    val canvas = Canvas(it)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                }
        }

        @SuppressLint("ResourceType")
        fun setWallpaper(drawable:Drawable, activity: Activity) {
            thread {
                val wallpaperManager = WallpaperManager.getInstance(activity.applicationContext)
                try {
                    val displayMetrics: DisplayMetrics = activity.applicationContext.resources.displayMetrics
                    val wallWidth = displayMetrics.widthPixels
                    val wallHeight = displayMetrics.heightPixels
                    wallpaperManager.suggestDesiredDimensions(wallWidth, wallHeight)
                    val bitmap = toBitmap(drawable)
                    val newBmap = returnBitmap(displayMetrics, bitmap, wallWidth, wallHeight)
                    if (newBmap != null) {
                        wallpaperManager.setBitmap(newBmap)
                    }
                    bitmap.recycle()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        private fun Int.nonZero() = if (this <= 0) 1 else this
    }
}