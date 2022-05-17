package com.android.mealme.fragments.home

import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.mealme.data.model.ResponseApiModel
import com.android.mealme.data.model.Restaurant
import com.android.mealme.data.service.RestaurantService
import com.android.mealme.data.service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    private val service = RetrofitClient.getInstance().create(RestaurantService::class.java)

    var restaurants = MutableLiveData<List<Restaurant>>().apply { value = emptyList() }
    var isLoading = MutableLiveData<Boolean>().apply { value = false }


    fun getRestaurants() {
        isLoading.value = true
        service.listRestaurants().enqueue(object: Callback<ResponseApiModel> {
            override fun onResponse(call: Call<ResponseApiModel>, response: Response<ResponseApiModel>) {
                isLoading.value = false
                if (response.isSuccessful){
                    response.body()?.restaurants?.let {
                        restaurants.value = it
                    }
                }
            }
            override fun onFailure(call: Call<ResponseApiModel>, error: Throwable) {
                isLoading.value = false
//                Toast.makeText(activity, "No tweets founds!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}