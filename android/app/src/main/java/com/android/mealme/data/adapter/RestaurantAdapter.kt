package com.android.mealme.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.mealme.R
import com.android.mealme.data.model.Restaurant
import com.android.mealme.data.service.RestaurantService
import com.android.mealme.data.service.RetrofitClient
import com.android.mealme.fragments.home.HomeFragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

interface RestaurantAdapterListener {
    fun onPressItem(restaurant: Restaurant)
}

class RestaurantAdapter (private val listener: RestaurantAdapterListener): RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {
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
            R.layout.item_restaurant -> {
                if (position <= itemCount){
                    val restaurant: Restaurant = _restaurants[position]
                    holder.itemView.findViewById<TextView>(R.id.restaurant_item_title).text = restaurant.name
                    holder.itemView.findViewById<TextView>(R.id.restaurant_item_location).text = restaurant.address.toString()

                    val imageView = holder.itemView.findViewById<ImageView>(R.id.restaurant_item_image)
                    val progressView = holder.itemView.findViewById<ProgressBar>(R.id.restaurant_item_loader)
                    Picasso.get().load(restaurant.logo_photos.first())
                        .into(imageView, object: Callback {
                            override fun onSuccess() {
                                progressView?.isVisible = false
                            }

                            override fun onError(e: Exception?) {
                                progressView?.isVisible = false
                                imageView.setImageResource(R.drawable.ic_baseline_image_not_supported)
                            }
                        })

                    holder.itemView.findViewById<CardView>(R.id.restaurant_item_card).setOnClickListener {
                        listener.onPressItem(restaurant)
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

    companion object {
        fun init(list: RecyclerView ,context: Context, _listener: RestaurantAdapterListener): RestaurantAdapter {
            val restaurantAdapter = RestaurantAdapter(_listener)
            list.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = restaurantAdapter
            }

            return restaurantAdapter
        }
    }
}