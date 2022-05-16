package com.android.mealme.fragments.register

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterViewModel : ViewModel() {
    private val email = MutableLiveData<String>()
    private val password = MutableLiveData<String>()

    val isLoading = MutableLiveData<Boolean>()

    fun setEmail(text:String){
        email.value = text
    }
    fun setPassword(text: String){
        password.value = text
    }

    fun register(ctx: Context): Task<AuthResult>? {
        isLoading.value = false
        val _email = email.value
        val _passowrd = password.value
        if(_email != null && _passowrd != null){
            isLoading.value = true
            return Firebase.auth.createUserWithEmailAndPassword(_email!!, _passowrd!!)
                .addOnCompleteListener {
                    isLoading.value = false
                }
                .addOnFailureListener {
                    Toast.makeText(ctx, "Ocurrió un error al realizar el registro del usuario", Toast.LENGTH_LONG).show()
                }
        }

        Toast.makeText(ctx, "Hay que llenar todos los campos para registrarse", Toast.LENGTH_LONG).show()
        return null
    }
}