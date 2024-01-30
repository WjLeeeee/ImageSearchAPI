package com.example.imagesave.ImageSearchFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.imagesave.data.CombinedSearchItem

class SearchViewModel : ViewModel() {

    private val _data: MutableLiveData<List<CombinedSearchItem>> = MutableLiveData()
    val data: LiveData<List<CombinedSearchItem>> get() = _data

    fun updateData(newData: List<CombinedSearchItem>){
        _data.value = newData
    }

}