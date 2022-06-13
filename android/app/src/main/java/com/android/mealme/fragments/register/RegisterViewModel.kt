package com.android.mealme.fragments.register

import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.mealme.R
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterViewModel : ViewModel() {
    private val email = MutableLiveData<String>()
    private val password = MutableLiveData<String>()
    private val repeatPassword = MutableLiveData<String>()

    val isLoading = MutableLiveData<Boolean>()

    fun setEmail(text:String){
        email.value = text
    }
    fun setPassword(text: String){
        password.value = text
    }
    fun setRepeatPassword(text: String){
        repeatPassword.value = text
    }

    fun register(view: View, resources: Resources, ctx: Context): Task<AuthResult>? {
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
                    Snackbar.make(
                        view,
                        resources.getString(R.string.errorRegister), Snackbar.LENGTH_LONG)
                        .show();
                }
        }

        Snackbar.make(
            view,
            resources.getString(R.string.errorFieldsRegister), Snackbar.LENGTH_LONG)
            .show();
        return null
    }

    fun validatePasswordRepeat(): Boolean {
        return password.value.equals(repeatPassword.value)
    }
}