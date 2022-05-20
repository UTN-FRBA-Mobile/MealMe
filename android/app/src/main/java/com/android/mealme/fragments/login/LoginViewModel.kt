package com.android.mealme.fragments.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel: ViewModel() {

    private val _email = MutableLiveData<String>();
    private val _password = MutableLiveData<String>();
    private val _isLoading = MutableLiveData<Boolean>();

    val email get() = _email;
    val password get() = _password;
    val isLoading get() = _isLoading;

    fun login(ctx: Context): Task<AuthResult>? {
        val firebaseAuth: FirebaseAuth = Firebase.auth
        if(!(email.value?.isNullOrEmpty()!! && password.value?.isNullOrEmpty()!!)){
            _isLoading.value = true;
            return firebaseAuth.signInWithEmailAndPassword(email.value.toString(), password.value.toString())
                .addOnCompleteListener {
                    _isLoading.value = false
                }
                .addOnFailureListener { error ->
                    val err: FirebaseAuthInvalidUserException = (error as FirebaseAuthInvalidUserException)
                    var message = ""
                    when(err.errorCode){
                        "ERROR_USER_NOT_FOUND" -> { message = "El usuario no está registrado"}
                        "ERROR_USER_DISABLED" -> { message = "El usuario está deshabilitado"}
                        "ERROR_USER_TOKEN_EXPIRED" -> { message = "Ocurrió un error al ingresar"}
                        "ERROR_INVALID_USER_TOKEN" -> { message = "Ocurrió un error al ingresar"}
                    }
                    Toast.makeText(ctx,message, Toast.LENGTH_LONG).show()
                }
        }
        return null
    }

    fun enableLogin(): Boolean {
        if(email.value.isNullOrEmpty() || password.value.isNullOrBlank()){
            return false;
        }
        return true
    }
}