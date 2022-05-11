package com.android.mealme.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Fragment para realizar una busqueda de restaurante"
    }
    val text: LiveData<String> = _text
}