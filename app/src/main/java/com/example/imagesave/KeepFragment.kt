package com.example.imagesave

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.imagesave.data.SearchDocument
import com.example.imagesave.data.SelectedItem
import com.example.imagesave.databinding.FragmentImageSearchBinding
import com.example.imagesave.databinding.FragmentKeepBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class KeepFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentKeepBinding? = null
    private val binding get() = _binding!!
    private lateinit var keepAdapter: KeepAdapter

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
        _binding = FragmentKeepBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * 실시간으로 업데이트하기위해 onResume상태일때 어뎁터 초기화.
     */
    override fun onResume() {
        keepAdapter = KeepAdapter(SelectedItem.myLikeList)
        binding.keepRecyclerView.adapter = keepAdapter
        binding.keepRecyclerView.layoutManager = GridLayoutManager(context, 2)
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                var result = SelectedItem(selectedThumb, selectedSite, selectedTime)
                SelectedItem.myLikeList.remove(result)
                keepAdapter.notifyDataSetChanged()
            }
        }
    }

    companion object {
        fun newInstance() = KeepFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}