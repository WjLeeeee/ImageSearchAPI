package com.example.imagesave.data

data class CombinedSearchItem(
    val searchItem: Any, // 이미지 또는 동영상
    val itemType: SearchItemType
)
enum class SearchItemType {
    IMAGE,
    VIDEO
}
