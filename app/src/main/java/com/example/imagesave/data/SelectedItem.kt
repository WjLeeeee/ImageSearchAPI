package com.example.imagesave.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SelectedItem(
    val thumbnail:String,
    val siteName:String,
    val time:String
): Parcelable{
    companion object{
        val myLikeList = mutableListOf<SelectedItem>()
    }
}
