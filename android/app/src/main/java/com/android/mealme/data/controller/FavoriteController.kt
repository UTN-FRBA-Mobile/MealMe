package com.android.mealme.data.controller

import androidx.lifecycle.MutableLiveData
import com.android.mealme.data.model.FavoriteRestaurant
import com.android.mealme.data.model.Restaurant
import com.android.mealme.data.service.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.CompletableFuture

class FavoriteController {
    private val firebaseInstance: FirebaseService = FirebaseService.instance

    var isLoading = MutableLiveData(true) // Start in loading
    var _favorites: MutableLiveData<List<Restaurant>> = MutableLiveData<List<Restaurant>>()
    val favorites get() = _favorites.value?.toList() ?: emptyList()

    private var listener: Unit

    constructor() {
        listener = FirebaseAuth.getInstance().addAuthStateListener {
            if (it.currentUser != null) {
                firebaseInstance.getFavorites().thenApply { favs -> _favorites.value = favs }
            }
        }
    }

    fun isRestaurantInFavorites(id: String): Boolean = favorites.find { it._id == id } != null

    fun getRestaurantsFromFavoritesIds(): List<Restaurant> = favorites

    fun getFavorites(): CompletableFuture<List<Restaurant>> {
        isLoading.value = true

        val future: CompletableFuture<List<Restaurant>> = CompletableFuture()
        FirebaseService.instance.getFavorites().thenApply {
            _favorites.value= it
            future.complete(it)
            isLoading.value = false
        }.exceptionally {
            future.complete(emptyList())
            isLoading.value = false
        }

        return future
    }

    fun addFavorite(favoriteRestaurant: FavoriteRestaurant): CompletableFuture<Boolean>? {
        return firebaseInstance.addFavorite(favoriteRestaurant)?.thenApply {
            if (it != null) {
                val newList = mutableListOf<Restaurant>()
                newList.addAll(favorites)
                newList.add(favoriteRestaurant.toRestaurant())
                _favorites.value = newList.toList()
            }
            true
        } ?: CompletableFuture.completedFuture(false)
    }

    fun removeFavorite(restaurantId: String): CompletableFuture<String>? {
        return firebaseInstance.removeFavorite(restaurantId)?.thenApply {
            _favorites.value = favorites.filter { resto: Restaurant ->
                resto._id != it
            }

            it
        } ?: CompletableFuture.completedFuture("")
    }

    companion object {
        var instance: FavoriteController = FavoriteController()
    }

}