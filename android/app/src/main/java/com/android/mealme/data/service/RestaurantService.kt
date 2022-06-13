package com.android.mealme.data.service

import com.android.mealme.data.model.ResponseApiModel
import com.android.mealme.data.model.Responses.CategoriesApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestaurantService {
    @GET("eaa7e2aa-8bd6-4954-b7cb-0b166862c529")
    fun listRestaurants(): Call<ResponseApiModel>

    @GET("26f3ebee-ed61-4293-9cf6-046e87ea82ce/{id}")
    fun getRestaurantDetail(@Path("id") id: String): Call<CategoriesApiResponse>

    @GET("27b5f529-b7aa-4807-9f17-72e56e9a8028?")
    fun listRestaurantsByGeo(@Query("latitude")latitude: Double, @Query("longitude")longitude: Double): Call<ResponseApiModel>

    @GET("eaa7e2aa-8bd6-4954-b7cb-0b166862c529")
    fun listRestaurantsByName(@Query("name")name:String): Call<ResponseApiModel>
}