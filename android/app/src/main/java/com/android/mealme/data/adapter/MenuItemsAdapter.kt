package com.android.mealme.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.mealme.R
import com.android.mealme.data.model.RestaurantMenuItem
import com.squareup.picasso.Picasso

class MenuItemsAdapter: RecyclerView.Adapter<MenuItemsAdapter.ViewHolder>() {
    private var list: List<RestaurantMenuItem> = emptyList()

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return  R.layout.item_menu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view: View = holder.itemView

        when(getItemViewType(position)){
            R.layout.item_menu -> {
                val item: RestaurantMenuItem = list.get(position)

                view.findViewById<TextView>(R.id.menu_item_title).text = item.name
                view.findViewById<TextView>(R.id.menu_item_description).text = item.description
                view.findViewById<TextView>(R.id.menu_item_price).text = "$${item.price.toString()}"

                Picasso.get().load(item.image).into(view.findViewById<ImageView>(R.id.menu_item_image))

            }
        }
    }

    fun setList(list: List<RestaurantMenuItem>){
        this.list = list
        this.notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}