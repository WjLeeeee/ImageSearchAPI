package com.example.imagesave.ImageSearchFragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagesave.KeepFragment.KeepFragment
import com.example.imagesave.data.CombinedSearchItem
import com.example.imagesave.data.SearchDocument
import com.example.imagesave.data.SearchDocumentVideo
import com.example.imagesave.data.SearchItemType
import com.example.imagesave.data.SelectedItem
import com.example.imagesave.databinding.FragmentImageSearchBinding

class ImageSearchFragment : Fragment() {
    private var _binding: FragmentImageSearchBinding? = null
    private val binding get() = _binding!!
    var items = mutableListOf<CombinedSearchItem>()
    private lateinit var searchAdapter: SearchAdapter
    private var currentPage = 1
    private val viewModel by lazy {
        ViewModelProvider(this)[SearchViewModel::class.java]
    }

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
        initViewModel()
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        searchAdapter.notifyDataSetChanged()
        initView()
        super.onResume()
    }

    /**
     * MVVM패턴 적용한부분 옵저빙
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun initViewModel() =with(viewModel){
        searchParam.observe(viewLifecycleOwner){
            communicateNetWork(it)
        }
        searchResult.observe(viewLifecycleOwner){
            items.clear()
            items.addAll(it)
            searchAdapter.notifyDataSetChanged()
        }
        loadData.observe(viewLifecycleOwner){
            binding.searchEdit.setText(it)
        }
    }

    private fun initView() = with(binding) {
        searchImage.setOnClickListener {
            saveData()
            val searchEdit = searchEdit.text.toString()
            if (searchEdit.isNotBlank()) {
                viewModel.setUpParameter(searchEdit, currentPage)
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
                val firstVisibleItemPosition =
                    layoutManager.findFirstVisibleItemPosition() //현재화면에 첫번째로보이는 아이템의 포지션
                val isLastItemVisible =
                    firstVisibleItemPosition + visibleItemCount >= totalItemCount -1 //마지막아이템이 화면에 보이는지
                if (isLastItemVisible && dy > 0) {
                    loadNextPage()
                }
            }
        })
        floatingBtn.setOnClickListener {
            searchRecyclerView.smoothScrollToPosition(0)
        }
        loadData()
        cancleDataChange()
        touchEvent()
    }

    /**
     * 아이템 클릭시
     */
    private fun touchEvent() {
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

    /**
     * 취소된 데이터 갱신
     */
    private fun cancleDataChange() {
        KeepFragment.myCancleList.forEach {
            for (item in searchAdapter.mItems) {
                when (item.itemType) {
                    SearchItemType.IMAGE -> {
                        val searchDocument = item.searchItem as SearchDocument
                        if (it == searchDocument.thumbnail_url) {
                            searchDocument.isLike = false
                            break
                        }
                    }

                    SearchItemType.VIDEO -> {
                        val searchDocumentVideo = item.searchItem as SearchDocumentVideo
                        if (it == searchDocumentVideo.thumbnail) {
                            searchDocumentVideo.isLike = false
                            break
                        }
                    }
                }
            }
        }
        KeepFragment.myCancleList.clear()
        searchAdapter.notifyDataSetChanged()
    }

    /**
     * 무한스크롤
     */
    private fun loadNextPage() {
        currentPage++
        val searchEdit = binding.searchEdit.text.toString()
        if (searchEdit.isNotBlank()) {
            viewModel.setUpParameter(searchEdit, currentPage)
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
    private fun communicateNetWork(param: HashMap<String, String>) {
        viewModel.dataFromNetwork(param)
    }

    /**
     * 키보드 숨기기
     */
    private fun View.hideKeyboardInput() { //UI관련된 코드이기 때문에 여기서 처리.
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    /**
     * 검색어 저장, 불러오기
     */
    private fun saveData() {
        viewModel.saveEditData(requireContext(), binding.searchEdit.text.toString())
    }

    private fun loadData() {
        viewModel.loadData(requireContext())
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}