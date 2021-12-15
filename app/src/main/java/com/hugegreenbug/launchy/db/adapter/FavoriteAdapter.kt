package com.hugegreenbug.launchy.db.adapter
import android.graphics.*
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.hugegreenbug.launchy.App
import com.hugegreenbug.launchy.AppManager
import com.hugegreenbug.launchy.R
import android.widget.RelativeLayout
import com.hugegreenbug.launchy.BitmapUtils

class FavoriteAdapter(private val appManager: AppManager, private var list: List<App>) :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>()  {
    var itemLongClickListener: (App, RelativeLayout) -> Boolean = { _, _ -> false }
    var itemKeyListener: (Int, KeyEvent, App, RelativeLayout, Int, Int) -> Boolean = { _, _, _, _, _, _ -> false }
    override fun getItemCount(): Int = list.size

    override fun getItemId(position: Int): Long {
        return list[position].hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.isEmpty()) return
        holder.bind(list[position], list.size, position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        private val ivFav: RelativeLayout = itemView.findViewById(R.id.ivFavorite)
        private val ivContainer: RelativeLayout = itemView.findViewById(R.id.ivContainer)

        fun bind(app: App, size: Int, position: Int) {
            itemView.setOnLongClickListener { itemLongClickListener.invoke(app, ivFav) }
            itemView.setOnKeyListener { _, code, event -> itemKeyListener.invoke(code, event, app,
                ivContainer, size, position) }
            itemView.setOnClickListener {
                appManager.startApp(app)
                return@setOnClickListener
            }

            val banner = appManager.getAppIcon(app.componentInfo)
            if (banner != null) {
                val bannerBmap = BitmapUtils.toBitmap(banner)
                val roundedBanner = getRoundedCornerBitmap(bannerBmap)
                ivIcon.setImageBitmap(roundedBanner)
            }
            if (app.favorite) {
                ivFav.visibility = View.VISIBLE
            } else {
                ivFav.visibility = View.INVISIBLE
            }
        }

        private fun getRoundedCornerBitmap(bitmap: Bitmap): Bitmap? {
            val pixels = 10
            val output = Bitmap.createBitmap(
                bitmap.width, bitmap
                    .height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)
            val roundPx = pixels.toFloat()
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }
    }
}