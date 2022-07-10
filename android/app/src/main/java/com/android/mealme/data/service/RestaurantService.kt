package com.android.mealme.data.service

import com.android.mealme.data.model.ResponseApiModel
import com.android.mealme.data.model.Responses.CategoriesApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestaurantService {
    @GET("restaurants")
    fun listRestaurants(): Call<ResponseApiModel>

    @GET("details/{id}")
    fun getRestaurantDetail(@Path("id") id: String): Call<CategoriesApiResponse>

    @GET("restaurants?")
    fun listRestaurantsByGeo(@Query("latitude")latitude: Double, @Query("longitude")longitude: Double): Call<ResponseApiModel>

    @GET("restaurants?")
    fun listRestaurantsByName(@Query("name")name:String?, @Query("address") address: String?): Call<ResponseApiModel>
}