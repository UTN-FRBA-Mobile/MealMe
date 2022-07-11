package com.android.mealme.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.mealme.R
import com.android.mealme.data.model.CustomizationOption
import com.android.mealme.data.model.MenuItemCustomization
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
                view.findViewById<TextView>(R.id.menu_item_price).text = calculatePrice(item.customizations)

                Picasso.get().load(item.image).into(view.findViewById<ImageView>(R.id.menu_item_image))

            }
        }
    }

    private fun calculatePrice(customizations: List<MenuItemCustomization>): CharSequence? {
        var price: Number = 0

        if(customizations.isNotEmpty()){
            price = findFirstValueNotNull(customizations[0].options, 0)
        }


        if(price != 0){
            if(price.toString().length > 3){
                return "$" + price.toString().subSequence(0,2) + "," +   price.toString().subSequence(2,price.toString().length)
            }else{
                return "$" + price.toString().subSequence(0,1) + "," +   price.toString().subSequence(1,price.toString().length)
            }
        }else
            return "$0"

    }

    private fun findFirstValueNotNull(options: List<CustomizationOption>, i: Int): Number {
        return if(options[i].price.toInt() != 0){
            options[i].price
        }else if(options.getOrNull(i+1) != null){
            findFirstValueNotNull(options, i+1)
        }else{
            if(options[0].customizations.isNotEmpty()){
                findFirstValueNotNull(options[0].customizations[0].options,0)
            }else{
                0
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