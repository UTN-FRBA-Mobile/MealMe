package com.android.mealme.fragments.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {
    private val address = MutableLiveData<String>()

    fun setAddress(text:String){
        address.value = text
    }

}