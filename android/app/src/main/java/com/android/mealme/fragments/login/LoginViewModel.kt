package com.android.mealme.fragments.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.mealme.data.controller.LoginController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CompletableFuture

class LoginViewModel : ViewModel() {
    val loginController = LoginController()

    private val _email = MutableLiveData<String>();
    private val _password = MutableLiveData<String>();
    private val _isLoading = MutableLiveData<Boolean>();

    val email get() = _email;
    val password get() = _password;
    val isLoading get() = _isLoading;

    fun login(ctx: Context): CompletableFuture<Boolean>? {
        val future = CompletableFuture<Boolean>()
        if (!(email.value?.isNullOrEmpty()!! && password.value?.isNullOrEmpty()!!)) {
            _isLoading.value = true;
            loginController
                .login(email.value.toString(), password.value.toString())
                .thenApply {
                    _isLoading.value = false
                    future.complete(true)
                }
                .exceptionally { error ->
                    val err: FirebaseAuthInvalidUserException =
                        (error as FirebaseAuthInvalidUserException)
                    var message = ""
                    when (err.errorCode) {
                        "ERROR_USER_NOT_FOUND" -> {
                            message = "El usuario no está registrado"
                        }
                        "ERROR_USER_DISABLED" -> {
                            message = "El usuario está deshabilitado"
                        }
                        "ERROR_USER_TOKEN_EXPIRED" -> {
                            message = "Ocurrió un error al ingresar"
                        }
                        "ERROR_INVALID_USER_TOKEN" -> {
                            message = "Ocurrió un error al ingresar"
                        }
                    }
                    Toast.makeText(ctx, message, Toast.LENGTH_LONG).show()
                    future.complete(false)
                }
        }
        return future
    }

    fun enableLogin(): Boolean {
        if (email.value.isNullOrEmpty() || password.value.isNullOrBlank()) {
            return false;
        }
        return true
    }
}