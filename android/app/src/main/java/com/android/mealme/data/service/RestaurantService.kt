package com.android.mealme.data.service

import com.android.mealme.data.model.ResponseApiModel
import retrofit2.Call
import retrofit2.http.GET

interface RestaurantService {
    @GET("eaa7e2aa-8bd6-4954-b7cb-0b166862c529")
    fun listRestaurants(): Call<ResponseApiModel>
}