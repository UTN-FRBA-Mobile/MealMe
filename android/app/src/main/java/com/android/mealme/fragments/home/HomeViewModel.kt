package com.android.mealme.fragments.home

import android.view.View
import android.widget.Button
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.mealme.data.controller.RestaurantController
import com.android.mealme.data.model.Restaurant
import com.android.mealme.data.service.RestaurantService
import com.android.mealme.data.service.RetrofitClient
import com.android.mealme.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    var restaurants = MutableLiveData<List<Restaurant>>().apply { value = emptyList() }
    var isLoading = MutableLiveData<Boolean>().apply { value = false }


    fun getRestaurants() {
        isLoading.value = true
        RestaurantController.instance.fetchRestaurants().thenApply {
            isLoading.value = false
            if(it.isNotEmpty()){
                restaurants.value = it
            }
        }
    }

    fun getRestaurantsByGeoLocation(
        latitude: Double,
        longitude: Double,
    ) {
        isLoading.value = true
        RestaurantController.instance.fetchByGeolocation(latitude, longitude).thenApply {
            isLoading.value = false
            restaurants.value = it

            it
        }
    }

    fun getRestaurantsByNameAndAddress(name: String?, address:String?){
        cleanRestaurants()
        isLoading.value = true
        RestaurantController.instance.fetchByNameOrAddress(name, address).thenApply {
            restaurants.value = it
            isLoading.value = false
        }
    }

    fun cleanRestaurants() {
        restaurants.value = emptyList()
    }
}