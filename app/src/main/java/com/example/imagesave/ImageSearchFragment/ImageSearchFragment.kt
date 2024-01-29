package com.example.imagesave.ImageSearchFragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.imagesave.Contract
import com.example.imagesave.data.CombinedSearchItem
import com.example.imagesave.data.SearchDocument
import com.example.imagesave.data.SearchDocumentVideo
import com.example.imagesave.data.SearchItemType
import com.example.imagesave.data.SelectedItem
import com.example.imagesave.databinding.FragmentImageSearchBinding
import com.example.imagesave.retrofit.NetWorkClient
import kotlinx.coroutines.launch

class ImageSearchFragment : Fragment() {
    private var _binding: FragmentImageSearchBinding? = null
    private val binding get() = _binding!!
    var items = mutableListOf<CombinedSearchItem>()
    private lateinit var searchAdapter: SearchAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        searchAdapter = SearchAdapter(items)
        binding.searchRecyclerView.adapter = searchAdapter
        binding.searchRecyclerView.layoutManager = GridLayoutManager(context, 2)
        super.onViewCreated(view, savedInstanceState)
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        searchAdapter.notifyDataSetChanged()
        initView()
        super.onResume()
    }
    private fun initView() =with(binding) {
        searchImage.setOnClickListener {
            saveData()
            val searchEdit = searchEdit.text.toString()
            if (searchEdit.isNotBlank()) {
                val searchParam = setUpImageParameter(searchEdit)
                communicateNetWork(searchParam)
            }
            binding.root.hideKeyboardInput()
        }
        loadData()
    }

    /**
     * 뷰페이져 적용
     */
    companion object {
        fun newInstance() = ImageSearchFragment()
    }

    /**
     * kakao API이용해서 사진 불러오기
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun communicateNetWork(param: HashMap<String, String>) = lifecycleScope.launch() {
        val authKey = "KakaoAK ${Contract.API_KEY}"
        // 이미지 검색
        val imageResponseData = NetWorkClient.combinedNetWork.getImageResults(authKey, param)
        imageResponseData.searchDocument?.let {
            items.addAll(it.map { CombinedSearchItem(it, SearchItemType.IMAGE) })
        }
        // 동영상 검색
        val videoResponseData = NetWorkClient.combinedNetWork.getVideoResults(authKey, param)
        videoResponseData.searchDocument?.let {
            items.addAll(it.map { CombinedSearchItem(it, SearchItemType.VIDEO) })
        }
        searchAdapter = SearchAdapter(items)
        binding.searchRecyclerView.adapter = searchAdapter
        searchAdapter.notifyDataSetChanged()
        searchAdapter.itemClick = object : SearchAdapter.ItemClick {
            override fun onClick(item: CombinedSearchItem, position: Int) {
                val selectedThumbnail:String
                val selectedSiteName:String
                val selectedTime:String
                when(item.itemType){
                    SearchItemType.IMAGE -> {
                        val result = item.searchItem as SearchDocument
                        selectedThumbnail = result.thumbnail_url
                        selectedSiteName = result.display_sitename
                        selectedTime = result.datetime
                        result.isLike = !result.isLike
                    }
                    SearchItemType.VIDEO -> {
                        val result = item.searchItem as SearchDocumentVideo
                        selectedThumbnail = result.thumbnail
                        selectedSiteName = result.title
                        selectedTime = result.datetime
                        result.isLike = !result.isLike
                    }
                }
                val selectedItems = SelectedItem(selectedThumbnail, selectedSiteName, selectedTime)
                if (SelectedItem.myLikeList.contains(selectedItems)) {
                    SelectedItem.myLikeList.remove(selectedItems)
                } else {
                    SelectedItem.myLikeList.add(selectedItems)
                }
            }

        }
    }
    private fun setUpImageParameter(input: String): HashMap<String, String> {
        val authKey = "KakaoAK ${Contract.API_KEY}"
        return hashMapOf(
            "Authorization" to authKey,
            "query" to input,
            "sort" to "recency",
            "page" to "1",
            "size" to "20"
        )
    }


    /**
     * 키보드 숨기기
     */
    private fun View.hideKeyboardInput() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    /**
     * sharedPreferences
     */
    private fun saveData() {
        val pref = requireContext().getSharedPreferences("pref",0)
        val edit = pref.edit()
        edit.putString("title", binding.searchEdit.text.toString())
        edit.apply() // 저장완료
    }
    private fun loadData() {
        val pref = requireContext().getSharedPreferences("pref",0)
        binding.searchEdit.setText(pref.getString("title",""))
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}