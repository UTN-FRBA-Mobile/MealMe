package com.android.mealme.fragments.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.mealme.data.controller.FavoriteController
import com.android.mealme.data.controller.RestaurantController
import com.android.mealme.data.model.Restaurant

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
}