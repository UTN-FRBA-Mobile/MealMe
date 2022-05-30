package com.android.mealme.data.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CompletableFuture

class FirebaseService {
    private val auth: FirebaseAuth = Firebase.auth
    private val database: FirebaseDatabase  = FirebaseDatabase.getInstance()

    val userId get() = auth.currentUser?.uid

    fun getFavorites(): CompletableFuture<Map<String, String>>? {
        if(userId != null){
            val completableFuture: CompletableFuture<Map<String, String>> = CompletableFuture()

            database.getReference("favorites/$userId").get()
                .addOnSuccessListener { snapshot ->
                    if(snapshot.value != null){
                        completableFuture.complete(snapshot.value as Map<String, String>)
                    }else {
                        completableFuture.complete(emptyMap())
                    }
                }.addOnFailureListener {
                    completableFuture.complete(emptyMap<String, String>())
                }
            return completableFuture
        }
        return null
    }

    fun addFavorite(restaurantId: String): CompletableFuture<String?>? {
        if(userId != null){
            val newData = database.getReference("favorites").child(userId!!).push()
            val completableFuture = CompletableFuture<String?>()
            newData.setValue(restaurantId).addOnCompleteListener {
                completableFuture.complete(if(it.isSuccessful) {newData.key} else null)
            }
            return completableFuture
        }
        return null
    }

    fun removeFavorite(restaurantId: String): CompletableFuture<Boolean>? {
        if(userId != null){
            val completableFuture =  CompletableFuture<Boolean>()
            database.getReference("favorites")
                .child(userId!!)
                .child(restaurantId)
                .removeValue()
                .addOnCompleteListener {
                    completableFuture.complete(it.isSuccessful)
                }

            return completableFuture
        }
        return null;
    }


    companion object {
        val instance = FirebaseService()
    }

}