package com.android.mealme.data.service

import com.android.mealme.data.model.ResponseApiModel
import retrofit2.Call
import retrofit2.http.GET

interface RestaurantService {
    @GET("list")
    fun listRestaurants(): Call<ResponseApiModel>
}