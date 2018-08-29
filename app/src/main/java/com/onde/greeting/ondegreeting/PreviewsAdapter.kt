package com.onde.greeting.ondegreeting

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.preview_item.view.*

/**
 * @author Artem Shaban
 * @since 8/30/18
 */

class PreviewsAdapter(private val images: List<Bitmap>, private val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.preview_item, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.image.setImageBitmap(images[position])
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val image = view.image
}