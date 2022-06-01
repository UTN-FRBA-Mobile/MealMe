package com.android.mealme.data.controller

import com.android.mealme.data.model.RestaurantReview
import com.android.mealme.data.service.FirebaseService
import java.util.concurrent.CompletableFuture

class ReviewController {
    private val firebase: FirebaseService = FirebaseService.instance
    private val _reviewsPerRestaurant: MutableMap<String, List<RestaurantReview>> = mutableMapOf()

    fun getReviewsForRestaurant(restaurantId: String): CompletableFuture<List<RestaurantReview>> {
        val future = CompletableFuture<List<RestaurantReview>>()
        firebase.getReviews(restaurantId).thenApply {
            _reviewsPerRestaurant.put(restaurantId, it)
            future.complete(it)

            it
        }
        return future
    }

    fun addReview(restaurantId: String, review: RestaurantReview): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        firebase.addReview(restaurantId, review).thenApply {
            future.complete(it)
            _reviewsPerRestaurant.apply {
                if (it.isNotEmpty()) {
                    val list: MutableList<RestaurantReview> = mutableListOf()
                    list.addAll(get(restaurantId)!!)
                    list.add(review)
                    replace(restaurantId, list)
                }
            }

            it
        }

        return future
    }

    companion object {
        val instance: ReviewController = ReviewController()
    }
}