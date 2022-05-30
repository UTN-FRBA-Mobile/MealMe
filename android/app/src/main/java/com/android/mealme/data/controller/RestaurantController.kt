package com.android.mealme.data.controller

import androidx.lifecycle.MutableLiveData
import com.android.mealme.data.model.ResponseApiModel
import com.android.mealme.data.model.Restaurant
import com.android.mealme.data.service.RestaurantService
import com.android.mealme.data.service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CompletableFuture

class RestaurantController {
    private val service = RetrofitClient.getInstance().create(RestaurantService::class.java)

    private val _restaurants = MutableLiveData<List<Restaurant>>(emptyList())
    val restaurants get() = _restaurants.value ?: emptyList()

    fun fetchRestaurants(): CompletableFuture<List<Restaurant>> {
        val future = CompletableFuture<List<Restaurant>>()
        service.listRestaurants().enqueue(object : Callback<ResponseApiModel> {
            override fun onResponse(
                call: Call<ResponseApiModel>,
                response: Response<ResponseApiModel>
            ) {
                if(response.isSuccessful){
                    _restaurants.postValue(response.body()?.restaurants)
                    future.complete(response.body()?.restaurants)
                }else {
                    future.complete(emptyList())
                }
            }

            override fun onFailure(call: Call<ResponseApiModel>, t: Throwable) {
                future.complete(emptyList())
            }
        })
        return future
    }

    companion object {
        val instance = RestaurantController()
    }
}