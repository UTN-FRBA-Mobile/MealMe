package com.android.mealme.fragments.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {
    private val _address: MutableLiveData<String> = MutableLiveData("")
    private val _name: MutableLiveData<String> = MutableLiveData("")

    val address: String get() = _address.value ?: ""
    val name: String get() = _name.value ?: ""

    fun setAddress(text: String) {
        this._address.value = text
    }

    fun setName(text: String){
        this._name.value = text
    }

}