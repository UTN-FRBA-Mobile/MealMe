package com.android.mealme.fragments.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.mealme.data.controller.FavoriteController
import com.android.mealme.data.controller.RestaurantController
import com.android.mealme.data.model.Restaurant

class FavoriteViewModel : ViewModel() {

    val favorites = MutableLiveData<List<Restaurant>>(emptyList())
}