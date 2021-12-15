package com.hugegreenbug.launchy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class UnsplashAdapter(private var list: List<Unsplash>) :
    RecyclerView.Adapter<UnsplashAdapter.ViewHolder>()  {
    var itemClickListener: (Unsplash) -> Unit = { }
    var itemLongClickListener: (Unsplash) -> Boolean = { _ -> false }

    override fun getItemCount(): Int = list.size

    override fun getItemId(position: Int): Long {
        return list[position].hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_unsplash, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.isEmpty()) return
        holder.bind(list[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val uwThumb: ImageView = itemView.findViewById(R.id.ivIcon)
        private val uwText: TextView = itemView.findViewById(R.id.ivText)

        fun bind(wall: Unsplash) {
            itemView.setOnClickListener {
                itemClickListener.invoke(wall)
            }

            itemView.setOnLongClickListener {
                itemLongClickListener.invoke(wall)
            }

            uwThumb.setImageBitmap(wall.thumb)

            val text = "Unsplash photo by: " + wall.author
            uwText.text = text
        }
    }


}