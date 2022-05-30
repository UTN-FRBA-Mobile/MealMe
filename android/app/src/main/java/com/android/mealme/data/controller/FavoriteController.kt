package com.android.mealme.data.controller

import androidx.lifecycle.MutableLiveData
import com.android.mealme.data.model.Restaurant
import com.android.mealme.data.service.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.CompletableFuture

class FavoriteController {
    private val firebaseInstance: FirebaseService = FirebaseService.instance

    var isLoading = MutableLiveData<Boolean>(true) // Start in loading
    var _favorites: MutableLiveData<MutableMap<String, String>> = MutableLiveData<MutableMap<String, String>>(mutableMapOf())
    val favorites get() = _favorites.value?.values?.toList() ?: emptyList()

    private var listener: Unit

    constructor() {
        listener = FirebaseAuth.getInstance().addAuthStateListener {
            if (it.currentUser != null) {
                _favorites.value?.clear()
                firebaseInstance.getFavorites()?.thenApply { favs -> _favorites.value?.putAll(favs) }
            }
        }
    }

    fun isRestaurantInFavorites(id: String): Boolean {
        return favorites.contains(id) ?: false
    }

    fun getRestaurantsFromFavoritesIds(restaurants: List<Restaurant> = RestaurantController.instance.restaurants): List<Restaurant> =
        restaurants.filter { restaurant ->
            favorites.contains(restaurant._id)
        }

    fun getFavorites(restaurants: List<Restaurant> = RestaurantController.instance.restaurants): CompletableFuture<List<Restaurant>>? {
        isLoading.value = true
        _favorites.value?.clear()
        if (restaurants.isNotEmpty()) {
            val future = CompletableFuture<List<Restaurant>>()
            future.thenAccept {
                isLoading.value = false
            }
            if (favorites.isNotEmpty()) {
                future.complete(getRestaurantsFromFavoritesIds())
            } else {
                firebaseInstance.getFavorites()?.thenApply {
                    _favorites.value?.putAll(it)
                    future.complete(getRestaurantsFromFavoritesIds())
                }
            }
            return future
        } else {
            return RestaurantController.instance.fetchRestaurants().thenCompose { restaurantList ->
                if (restaurantList.isNotEmpty()) {
                    return@thenCompose getFavorites(restaurantList)
                }
                CompletableFuture()
            }
        }
        return null
    }

    fun addFavorite(restaurantId: String): CompletableFuture<Boolean>? {
        return firebaseInstance.addFavorite(restaurantId)?.thenApply {
            if(it != null){
                _favorites.value?.put(it, restaurantId)
            }
            true
        } ?: CompletableFuture.completedFuture(false)
    }

    fun removeFavorite(restaurantId: String): CompletableFuture<Boolean>? {
        val index = favorites.indexOf(restaurantId)
        if (index >= 0) {
            val key = _favorites.value?.keys?.toList()?.get(index)
            return firebaseInstance.removeFavorite(key!!)?.thenApply {
                _favorites.value?.clear()
                _favorites.value?.putAll(_favorites.value?.filterKeys { it == key }!!)
                true
            } ?: CompletableFuture.completedFuture(false)
        }
        return null
    }

    companion object {
        var instance: FavoriteController = FavoriteController()
    }

}