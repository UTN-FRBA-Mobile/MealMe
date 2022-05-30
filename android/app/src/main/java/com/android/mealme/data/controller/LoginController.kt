package com.android.mealme.data.controller

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import java.util.concurrent.CompletableFuture

class LoginController {
    private val instance = FirebaseAuth.getInstance()

    fun login(email: String, password: String): CompletableFuture<Optional<AuthResult>> {
        val future = CompletableFuture<Optional<AuthResult>>()
        instance.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                FavoriteController.instance.getFavorites()
                future.complete(Optional.of(it))
            }.addOnFailureListener {
                future.obtrudeException(it)
            }
        return future
    }
}