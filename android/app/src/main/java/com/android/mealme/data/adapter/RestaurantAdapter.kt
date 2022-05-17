package com.android.mealme.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.mealme.R
import com.android.mealme.data.model.Restaurant
import com.android.mealme.data.service.RestaurantService
import com.android.mealme.data.service.RetrofitClient
import com.android.mealme.fragments.home.HomeFragment
import com.squareup.picasso.Picasso

class RestaurantAdapter (private val listener: HomeFragment.OnFragmentInteractionListener?): RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {
    private var _restaurants: List<Restaurant> = emptyList()

    override fun getItemViewType(position: Int): Int {
        return  R.layout.item_restaurant
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_simple, R.layout.item_restaurant -> {
                if (position <= itemCount){
                    val it = _restaurants[position]
                    if (it != null){
                        holder.itemView.findViewById<TextView>(R.id.restaurant_item_title).text = it.name
                        holder.itemView.findViewById<TextView>(R.id.restaurant_item_location).text = it.address.toString()

                        val imageView = holder.itemView.findViewById<ImageView>(R.id.restaurant_item_image)
                        Picasso.get().load(it.logo_photos.first()).into(imageView)
                    }
                }
            }
            else -> {}
        }
    }

    override fun getItemCount(): Int = _restaurants.size // el primer item es el encabezado

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    fun setRestaurants(restaurants: List<Restaurant>){
        _restaurants = restaurants
        notifyDataSetChanged()
    }
}