package com.example.imagesave.KeepFragment

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.imagesave.data.SelectedItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class KeepViewModel : ViewModel() {
    private val _loadListData: MutableLiveData<List<SelectedItem>> = MutableLiveData()
    val loadListData: LiveData<List<SelectedItem>> get() = _loadListData

    fun saveMyLikeList(context: Context, myLikeList: List<SelectedItem>) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", 0)
        val editor = sharedPreferences.edit()
        // 데이터를 문자열로 변환하여 저장
        val jsonMyLikeList = Gson().toJson(myLikeList)
        editor.putString("myLikeList", jsonMyLikeList)
        editor.apply()
    }
    fun loadMyLikeList(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", 0)
        val jsonMyLikeList = sharedPreferences.getString("myLikeList", null)
        // 저장된 데이터가 있으면 해당 데이터를 객체로 변환하여 대입
        jsonMyLikeList?.let {
            val type = object : TypeToken<List<SelectedItem>>() {}.type
            _loadListData.value = Gson().fromJson(it, type)
        }
    }
}