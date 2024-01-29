package com.example.imagesave.ImageSearchFragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private var currentPage = 1
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

    private fun initView() = with(binding) {
        searchImage.setOnClickListener {
            saveData()
            val searchEdit = searchEdit.text.toString()
            if (searchEdit.isNotBlank()) {
                val searchParam = setUpImageParameter(searchEdit, currentPage)
                communicateNetWork(searchParam)
            }
            binding.root.hideKeyboardInput()
        }
        /**
         * 최상단으로 이동시키는 플로팅버튼
         */
        searchRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 1000 }
            val fadeOut = AlphaAnimation(1f, 0f).apply { duration = 1000 }
            var isTop = true
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!searchRecyclerView.canScrollVertically(-1)
                    && newState == RecyclerView.SCROLL_STATE_IDLE
                ) {
                    floatingBtn.startAnimation(fadeOut)
                    floatingBtn.visibility = View.GONE
                    isTop = true
                } else {
                    if (isTop) {
                        floatingBtn.visibility = View.VISIBLE
                        floatingBtn.startAnimation(fadeIn)
                        isTop = false
                    }
                }
            }

            /**
             * 다음페이지 자동 검색 구현
             */
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount //화면에보이는 아이템개수
                val totalItemCount = layoutManager.itemCount //어뎁터에설정된 전체아이템개수
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition() //현재화면에 첫번째로보이는 아이템의 포지션
                val isLastItemVisible = firstVisibleItemPosition + visibleItemCount >= totalItemCount //마지막아이템이 화면에 보이는지
                if (isLastItemVisible) {
                    loadNextPage()
                }
            }
        })
        floatingBtn.setOnClickListener {
            searchRecyclerView.smoothScrollToPosition(0)
        }
        loadData()
    }
    private fun loadNextPage(){
        currentPage++
        val searchEdit = binding.searchEdit.text.toString()
        if (searchEdit.isNotBlank()) {
            val searchParam = setUpImageParameter(searchEdit, currentPage)
            communicateNetWork(searchParam)
        }
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
        //기존 검색기록은 삭제
        items.clear()

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

        searchAdapter = SearchAdapter(items)
        binding.searchRecyclerView.adapter = searchAdapter
        searchAdapter.notifyDataSetChanged()
        searchAdapter.itemClick = object : SearchAdapter.ItemClick {
            override fun onClick(item: CombinedSearchItem) {
                val selectedThumbnail: String
                val selectedSiteName: String
                val selectedTime: String
                when (item.itemType) {
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

    private fun setUpImageParameter(input: String, page:Int): HashMap<String, String> {
        val authKey = "KakaoAK ${Contract.API_KEY}"
        return hashMapOf(
            "Authorization" to authKey,
            "query" to input,
            "sort" to "recency",
            "page" to page.toString(),
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
        val pref = requireContext().getSharedPreferences("pref", 0)
        val edit = pref.edit()
        edit.putString("title", binding.searchEdit.text.toString())
        edit.apply() // 저장완료
    }

    private fun loadData() {
        val pref = requireContext().getSharedPreferences("pref", 0)
        binding.searchEdit.setText(pref.getString("title", ""))
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}