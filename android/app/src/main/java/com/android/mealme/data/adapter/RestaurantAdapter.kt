package com.android.mealme.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.mealme.R
import com.android.mealme.data.model.RestaurantModel
import com.android.mealme.fragments.home.HomeFragment

class RestaurantAdapter (private val listener: HomeFragment.OnFragmentInteractionListener?): RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {
    private var _restaurants: List<RestaurantModel> = emptyList()

    override fun getItemViewType(position: Int): Int {
        return  R.layout.item_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        when (viewType) {
            R.layout.item_post -> {
                view.findViewById<Button>(R.id.postButton).setOnClickListener {
                    listener?.showFragment(StatusUpdateFragment())
                }
            }
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_simple, R.layout.item_image -> {
                if (position <= itemCount){
                    val it = _restaurants[position]
                    if (it != null){
                        holder.itemView.findViewById<TextView>(R.id.restaurantName).text = "${it.name}"
                        holder.itemView.findViewById<TextView>(R.id.restaurantAddress).text = "Dirección provisoria"


                    }
                }
            }
            else -> {}
        }
    }

    override fun getItemCount(): Int = _restaurants.size // el primer item es el encabezado

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    fun setRestaurants(restaurants: List<RestaurantModel>){
        _restaurants = restaurants
    }
}