package com.example.imagesave.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.imagesave.ImageSearchFragment.ImageSearchFragment
import com.example.imagesave.KeepFragment.KeepFragment
import com.example.imagesave.R

class MainViewPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private val fragments= listOf(
        MainTab(
            fragment = ImageSearchFragment.newInstance(),
            title = R.string.main_tab_search
        ),
        MainTab(
            fragment = KeepFragment.newInstance(),
            title = R.string.main_tab_keep
        )
    )
    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position].fragment
    fun getTitle(position: Int): Int = fragments[position].title

}