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
import com.example.imagesave.KeepFragment.KeepFragment
import com.example.imagesave.KeepFragment.OnThumbnailClickListener
import com.example.imagesave.data.SearchDocument
import com.example.imagesave.data.SelectedItem
import com.example.imagesave.databinding.FragmentImageSearchBinding
import com.example.imagesave.retrofit.NetWorkClient
import kotlinx.coroutines.launch

class ImageSearchFragment : Fragment(), OnThumbnailClickListener {
    private var _binding: FragmentImageSearchBinding? = null
    private val binding get() = _binding!!
    var items = mutableListOf<SearchDocument>()
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
//        val parentFragment = parentFragment
//        if (parentFragment is KeepFragment) {
//            parentFragment.thumbnailClickListener = this
//        }

        Log.d("ImageSearchFragment", "parentFragment: $parentFragment")


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
    override fun onThumbnailClick(thumbnail: String) {
        Log.d("ImageSearchFragment", "Thumbnail Received: $thumbnail")
        updateSearchAdapter(thumbnail)
    }
    private fun updateSearchAdapter(thumbnail: String) {
        val itemToUpdate = items.find { it.thumbnail_url == thumbnail }
        itemToUpdate?.isLike = false
        searchAdapter.notifyItemChanged(items.indexOf(itemToUpdate))
        Log.d("ImageSearchFragment", "Thumbnail Updated: $thumbnail")

    }
    companion object {
        fun newInstance() = ImageSearchFragment()
    }

    /**
     * kakao API이용해서 사진 불러오기
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun communicateNetWork(param: HashMap<String, String>) = lifecycleScope.launch() {
        val authKey = "KakaoAK ${Contract.API_KEY}"
        val responseData = NetWorkClient.imageNetWork.getImage(authKey, param)
        items = responseData.searchDocument ?: mutableListOf()
        searchAdapter = SearchAdapter(items)
        binding.searchRecyclerView.adapter = searchAdapter
        searchAdapter.notifyDataSetChanged()
        searchAdapter.itemClick = object : SearchAdapter.ItemClick {
            override fun onClick(item: SearchDocument, position: Int) {
                val selectedThumbnail = item.thumbnail_url
                val selectedSiteName = item.display_sitename
                val selectedTime = item.datetime
                item.isLike = !item.isLike
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