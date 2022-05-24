package com.android.mealme.data.service

import com.android.mealme.data.model.ResponseApiModel
import com.android.mealme.data.model.Responses.CategoriesApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RestaurantService {
    @GET("eaa7e2aa-8bd6-4954-b7cb-0b166862c529")
    fun listRestaurants(): Call<ResponseApiModel>

    @GET("26f3ebee-ed61-4293-9cf6-046e87ea82ce/{id}")
    fun getRestaurantDetail(@Path("id") id: String): Call<CategoriesApiResponse>
}