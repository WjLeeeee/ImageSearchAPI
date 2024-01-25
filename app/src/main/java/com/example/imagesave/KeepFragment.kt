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
    var items = mutableListOf<SelectedItem>()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = KeepAdapter(SelectedItem.myLikeList)
        binding.keepRecyclerView.adapter = adapter
        binding.keepRecyclerView.layoutManager = GridLayoutManager(context, 2)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            KeepFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}