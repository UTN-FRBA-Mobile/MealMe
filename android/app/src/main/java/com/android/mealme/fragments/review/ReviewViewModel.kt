package com.android.mealme.fragments.review

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.mealme.data.controller.RestaurantController
import com.android.mealme.data.controller.ReviewController
import com.android.mealme.data.model.Restaurant
import com.android.mealme.data.model.RestaurantReview

class ReviewViewModel: ViewModel() {
    private val controller: ReviewController = ReviewController.instance
    val restaurant: MutableLiveData<Restaurant> = MutableLiveData()

    val _score : MutableLiveData<String> = MutableLiveData("")
    val score get() = _score.value!! ?: ""
    val _message : MutableLiveData<String> = MutableLiveData("")
    val message get() = _message.value!! ?: ""

    val isLoading = MutableLiveData<Boolean>(false)

    fun setRestaurantId(restaurantId: String){
        restaurant.value = RestaurantController.instance.restaurants.find { it._id == restaurantId }
    }

    fun addReview(cb: Runnable) {
        isLoading.value = true
        val review = RestaurantReview(score.split(".")[0].toInt(), message)
        controller.addReview(restaurant.value?._id!!, review).thenApply {
            isLoading.value = false
            cb.run()
        }
    }

    fun setMessageText(msg: String){
        _message.value = msg
    }
    fun setScore(scr: Float){
        _score.value = scr.toString()
    }
}