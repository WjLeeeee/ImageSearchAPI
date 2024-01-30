package com.example.imagesave.KeepFragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.imagesave.ImageSearchFragment.SearchViewModel
import com.example.imagesave.R
import com.example.imagesave.data.SearchDocument
import com.example.imagesave.data.SelectedItem
import com.example.imagesave.databinding.FragmentKeepBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


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
        loadMyLikeList()
        keepAdapter = KeepAdapter(SelectedItem.myLikeList)
        binding.keepRecyclerView.adapter = keepAdapter
        binding.keepRecyclerView.layoutManager = GridLayoutManager(context, 2)

        return binding.root
    }

    /**
     * 실시간으로 업데이트하기위해 onResume상태일때 어뎁터 초기화.
     */
    override fun onResume() {
        initViewModel()
        keepAdapter = KeepAdapter(SelectedItem.myLikeList)
        binding.keepRecyclerView.adapter = keepAdapter
        binding.keepRecyclerView.layoutManager = GridLayoutManager(context, 2)
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

    private fun initViewModel() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    private fun saveMyLikeList() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", 0)
        val editor = sharedPreferences.edit()
        // 데이터를 문자열로 변환하여 저장
        val jsonMyLikeList = Gson().toJson(SelectedItem.myLikeList)
        editor.putString("myLikeList", jsonMyLikeList)
        editor.apply()
    }

    private fun loadMyLikeList() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", 0)
        val jsonMyLikeList = sharedPreferences.getString("myLikeList", null)
        // 저장된 데이터가 있으면 해당 데이터를 객체로 변환하여 대입
        jsonMyLikeList?.let {
            val type = object : TypeToken<List<SelectedItem>>() {}.type
            SelectedItem.myLikeList = Gson().fromJson(it, type)
        }
    }

    companion object {
        fun newInstance() = KeepFragment()
        var myCancleList = mutableListOf<String>()
    }

    override fun onPause() {
        super.onPause()
        saveMyLikeList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}