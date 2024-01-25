package com.example.imagesave

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
import com.example.imagesave.data.SearchDocument
import com.example.imagesave.data.SelectedItem
import com.example.imagesave.databinding.FragmentImageSearchBinding
import com.example.imagesave.retrofit.NetWorkClient
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ImageSearchFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentImageSearchBinding? = null
    private val binding get() = _binding!!
    var items = mutableListOf<SearchDocument>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
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

    companion object {
        fun newInstance() = ImageSearchFragment()
    }

    /**
     * kakao API이용해서 사진 불러오기
     */
    private fun communicateNetWork(param: HashMap<String, String>) = lifecycleScope.launch() {
        val authKey = "KakaoAK ${Contract.API_KEY}"
        val responseData = NetWorkClient.imageNetWork.getImage(authKey, param)
        items = responseData.searchDocument ?: mutableListOf()
        val adapter = SearchAdapter(items)

        adapter.itemClick = object : SearchAdapter.ItemClick{
            override fun onClick(item: SearchDocument, position: Int) {
                handleSelectedItem(item)
            }
        }

        binding.searchRecyclerView.adapter = adapter
        binding.searchRecyclerView.layoutManager = GridLayoutManager(context, 2)
    }

    /**
     * 리사이클러뷰 아이템클릭시 리스트에 저장하기
     */
    private fun handleSelectedItem(item: SearchDocument) {
        val selectedThumnail = item.thumbnail_url
        val selectedSiteName = item.display_sitename
        val selectedTime = item.datetime
        val selectedItems = SelectedItem(selectedThumnail, selectedSiteName, selectedTime)
        if(SelectedItem.myLikeList.contains(selectedItems)){
            SelectedItem.myLikeList.remove(selectedItems)
        } else{
            SelectedItem.myLikeList.add(selectedItems)
        }
        Log.d("selected", "myLikeList : ${SelectedItem.myLikeList}")
    }

    private fun setUpImageParameter(input: String): HashMap<String, String> {
        val authKey = "KakaoAK ${Contract.API_KEY}"
        return hashMapOf(
            "Authorization" to authKey,
            "query" to input,
            "sort" to "recency",
            "page" to "1",
            "size" to "80"
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