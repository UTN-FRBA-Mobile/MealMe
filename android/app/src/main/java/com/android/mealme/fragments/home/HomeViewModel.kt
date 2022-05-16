package com.android.mealme.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Home con listado de restaurantes cercanos y últimos visitados"
    }
    val text: LiveData<String> = _text
}