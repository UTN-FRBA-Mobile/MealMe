package com.android.mealme.fragments.restaurantDetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.mealme.data.controller.FavoriteController
import com.android.mealme.data.model.Responses.CategoriesApiResponse
import com.android.mealme.data.model.Restaurant
import com.android.mealme.data.model.RestaurantCategory
import com.android.mealme.data.service.RestaurantService
import com.android.mealme.data.service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantDetailViewModel : ViewModel() {
    private val service = RetrofitClient.getInstance().create(RestaurantService::class.java)

    var _restaurant: MutableLiveData<Restaurant> =
        MutableLiveData<Restaurant>().apply { value = null }
    val restaurant get() = _restaurant.value

    var categories: MutableLiveData<List<RestaurantCategory>> =
        MutableLiveData<List<RestaurantCategory>>().apply { value = emptyList() }

    var isFavorite: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false } // TODO: set false as default

    var isLoading: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }

    fun setRestaurant(restaurant: Restaurant) {
        this._restaurant.value = restaurant;
        fetchDetail()
    }

    override fun onCleared() {
        super.onCleared()
    }

    private fun fetchDetail() {
        isLoading.value = true
        service.getRestaurantDetail(restaurant?._id!!)
            .enqueue(object : Callback<CategoriesApiResponse> {
                override fun onResponse(
                    call: Call<CategoriesApiResponse>,
                    response: Response<CategoriesApiResponse>
                ) {
                    isLoading.value = false
                    if (response.isSuccessful) {
                        categories.value = response.body()?.categories!!
                    } else {
                        // TODO: set error
                    }
                }

                override fun onFailure(call: Call<CategoriesApiResponse>, t: Throwable) {
                    isLoading.value = false
                    //TODO: set error
                }
            })
    }

    fun addFavorite() {
        isFavorite.value = true
        FavoriteController.instance.addFavorite(restaurant?._id!!)?.thenApply {
            isFavorite.value = it
        }
    }


    fun removeFavorite() {
        isFavorite.value = false
        FavoriteController.instance.removeFavorite(restaurant?._id!!)?.thenApply {
            isFavorite.value = !it
        }
    }

}