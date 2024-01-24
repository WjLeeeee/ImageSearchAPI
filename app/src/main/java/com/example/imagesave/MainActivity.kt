package com.example.imagesave

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.imagesave.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    //리스트 만들어서 좋아요한 이미지 저장, keepFragment에서는 해당값을 뿌림
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding){
            searchBtn.setOnClickListener {
                setFragment(ImageSearchFragment())
            }
            keepBtn.setOnClickListener {
                setFragment(KeepFragment())
            }
            setFragment(ImageSearchFragment())
        }
    }

    private fun setFragment(frag : Fragment) {
        supportFragmentManager.commit {
            replace(R.id.frameLayout, frag)
            setReorderingAllowed(true)
        }
    }
}