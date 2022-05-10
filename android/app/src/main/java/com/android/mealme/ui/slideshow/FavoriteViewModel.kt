package com.android.mealme.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoriteViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Fragment para ver un listado de restaurantes favoritos"
    }
    val text: LiveData<String> = _text
}