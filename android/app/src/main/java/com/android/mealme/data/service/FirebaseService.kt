package com.android.mealme.data.service

import android.annotation.SuppressLint
import com.android.mealme.data.controller.FavoriteController
import com.android.mealme.data.model.FavoriteRestaurant
import com.android.mealme.data.model.Restaurant
import com.android.mealme.data.model.RestaurantReview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.HashMap

class FirebaseService {
    private val auth: FirebaseAuth = Firebase.auth
    private val database: FirebaseDatabase  = FirebaseDatabase.getInstance()

    val userId get() = auth.currentUser?.uid

    fun getFavorites(): CompletableFuture<List<Restaurant>> {
        val completableFuture: CompletableFuture<List<Restaurant>> = CompletableFuture()
        if(userId != null){

            database.getReference("favorites/$userId").get()
                .addOnSuccessListener { snapshot ->
                    if(snapshot.value != null){
                        val favorites = (snapshot.value as HashMap<String, String>).values.map {
                            Gson().fromJson(it, FavoriteRestaurant::class.java).toRestaurant()
                        }
                        completableFuture.complete(favorites)
                    }else {
                        completableFuture.complete(emptyList())
                    }
                }.addOnFailureListener {
                    completableFuture.complete(emptyList())
                }
        }
        return completableFuture
    }

    fun addFavorite(favoriteRestaurant: FavoriteRestaurant): CompletableFuture<String?>? {
        if(userId != null){
            val newData = database.getReference("favorites").child(userId!!).child(favoriteRestaurant._id)
            val completableFuture = CompletableFuture<String?>()
            newData.setValue(Gson().toJson(favoriteRestaurant)).addOnCompleteListener {
                completableFuture.complete(if(it.isSuccessful) {newData.key} else null)
            }
            return completableFuture
        }
        return null
    }

    fun removeFavorite(restaurantId: String): CompletableFuture<String>? {
        if(userId != null){
            val completableFuture =  CompletableFuture<String>()
            database.getReference("favorites")
                .child(userId!!)
                .child(restaurantId)
                .removeValue()
                .addOnSuccessListener { completableFuture.complete(restaurantId) }
                .addOnFailureListener { completableFuture.complete("") }

            return completableFuture
        }
        return null;
    }

    fun getReviews(restaurantId: String): CompletableFuture<List<RestaurantReview>> {
        val future = CompletableFuture<List<RestaurantReview>>()
        database.getReference("reviews").child(restaurantId).get().addOnCompleteListener {
            if(it.isSuccessful){

            }
            future.complete(emptyList())
        }
        return future
    }

    fun addReview(restaurantId: String, review: RestaurantReview): CompletableFuture<String> {
        val future = CompletableFuture<String>()
        val reference = database.getReference("reviews").child(restaurantId)
        val newData = reference.push()
        newData.setValue(review).addOnCompleteListener {
            future.complete(if(it.isSuccessful) {newData.key} else null)
        }
        return future
    }

    companion object {
        val instance = FirebaseService()
    }

}