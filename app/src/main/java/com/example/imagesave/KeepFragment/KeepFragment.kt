package com.example.imagesave.KeepFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.imagesave.data.SelectedItem
import com.example.imagesave.databinding.FragmentKeepBinding



class KeepFragment : Fragment() {
    private var _binding: FragmentKeepBinding? = null
    private val binding get() = _binding!!
    private lateinit var keepAdapter: KeepAdapter
    private val viewModel by lazy {
        ViewModelProvider(this)[KeepViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKeepBinding.inflate(inflater, container, false)
        initViewModel()
        loadMyLikeList()

        return binding.root
    }
    private fun initViewModel() =with(viewModel) {
        loadListData.observe(viewLifecycleOwner){
            SelectedItem.myLikeList = it.toMutableList()
            keepAdapter = KeepAdapter(SelectedItem.myLikeList)
            binding.keepRecyclerView.adapter = keepAdapter
            binding.keepRecyclerView.layoutManager = GridLayoutManager(context, 2)
        }
    }

    /**
     * 실시간으로 업데이트하기위해 onResume상태일때 어뎁터 초기화.
     */
    override fun onResume() {
        keepAdapter.notifyDataSetChanged()
        /**
         * 아이템 선택시 아이템 삭제, 어뎁터 갱신
         */
        keepAdapter.itemClick = object : KeepAdapter.ItemClick {
            override fun onClick(item: SelectedItem) {
                val selectedThumb = item.thumbnail
                val selectedSite = item.siteName
                val selectedTime = item.time
                myCancleList.add(selectedThumb)
                var result = SelectedItem(selectedThumb, selectedSite, selectedTime)
                SelectedItem.myLikeList.remove(result)
                keepAdapter.notifyDataSetChanged()
            }
        }
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * 클릭해서 저장된 정보 저장하기
     */
    private fun saveMyLikeList() {
        viewModel.saveMyLikeList(requireContext(), SelectedItem.myLikeList)
    }

    /**
     * 저장된 정보 불러오기
     */
    private fun loadMyLikeList() {
        viewModel.loadMyLikeList(requireContext())
    }

    /**
     * 뷰페이저 적용, 취소리스트저장
     */
    companion object {
        fun newInstance() = KeepFragment()
        var myCancleList = mutableListOf<String>()
    }

    /**
     * 앱종료시 데이터 저장
     */
    override fun onPause() {
        super.onPause()
        saveMyLikeList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}