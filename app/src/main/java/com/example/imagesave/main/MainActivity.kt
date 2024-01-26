package com.example.imagesave.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.imagesave.data.SelectedItem
import com.example.imagesave.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(){
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewPagerAdapter by lazy {
        MainViewPagerAdapter(this@MainActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
    }

    private fun initView() = with(binding) {
        viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            tab.setText(viewPagerAdapter.getTitle(position))
        }.attach()
    }
}