package com.example.imagesave.ImageSearchFragment

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagesave.Contract
import com.example.imagesave.data.CombinedSearchItem
import com.example.imagesave.data.SearchDocument
import com.example.imagesave.data.SearchDocumentVideo
import com.example.imagesave.data.SearchItemType
import com.example.imagesave.retrofit.NetWorkClient
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _searchParam: MutableLiveData<HashMap<String, String>> = MutableLiveData()
    val searchParam: LiveData<HashMap<String, String>> get() = _searchParam
    private val _searchResult: MutableLiveData<List<CombinedSearchItem>> = MutableLiveData()
    val searchResult: LiveData<List<CombinedSearchItem>> get() = _searchResult
    private val _loadData: MutableLiveData<String?> = MutableLiveData()
    val loadData: LiveData<String?> get() = _loadData


    fun setUpParameter(input: String, page: Int) {
        val authKey = "KakaoAK ${Contract.API_KEY}"
        _searchParam.value = hashMapOf(
            "Authorization" to authKey,
            "query" to input,
            "sort" to "recency",
            "page" to page.toString(),
            "size" to "20"
        )
    }
    fun dataFromNetwork(param: HashMap<String, String>) {
        viewModelScope.launch {
            val authKey = "KakaoAK ${Contract.API_KEY}"
            val items = mutableListOf<CombinedSearchItem>()

            val imageResponseData = NetWorkClient.combinedNetWork.getImageResults(authKey, param)
            val videoResponseData = NetWorkClient.combinedNetWork.getVideoResults(authKey, param)

            imageResponseData.searchDocument?.let {
                items.addAll(it.map { CombinedSearchItem(it, SearchItemType.IMAGE) })
            }
            videoResponseData.searchDocument?.let {
                items.addAll(it.map { CombinedSearchItem(it, SearchItemType.VIDEO) })
            }

            items.shuffle()
            items.sortedByDescending {
                when (it.itemType) {
                    SearchItemType.IMAGE -> (it.searchItem as SearchDocument).datetime
                    SearchItemType.VIDEO -> (it.searchItem as SearchDocumentVideo).datetime
                }
            }

            _searchResult.value = items
        }
    }

    fun saveEditData(context: Context, searchEdit:String) {
        val pref = context.getSharedPreferences("pref", 0)
        val edit = pref.edit()
        edit.putString("title", searchEdit)
        edit.apply() // 저장완료
    }
    fun loadData(context: Context) {
        val pref = context.getSharedPreferences("pref", 0)
        val loadData = pref.getString("title", "")
        _loadData.value = loadData
    }

}