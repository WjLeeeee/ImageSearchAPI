package com.example.imagesave.data

import com.google.gson.annotations.SerializedName

//이미지 검색
data class SearchResponse(
    @SerializedName("meta")
    val searchMeta: SearchMeta,
    @SerializedName("documents")
    val searchDocument: MutableList<SearchDocument>?
)

data class SearchMeta(
    val is_end: Boolean,
    val pageable_count: Int,
    val total_count: Int
)
data class SearchDocument(
    val collection: String,
    val datetime: String,
    val display_sitename: String,
    val doc_url: String,
    val height: Int,
    val image_url: String,
    val thumbnail_url: String,
    val width: Int,
    var isLike: Boolean = false
)

//비디오 검색
data class SearchResponseVideo(
    @SerializedName("meta")
    val searchMeta: SearchMetaVideo,
    @SerializedName("documents")
    val searchDocument: MutableList<SearchDocumentVideo>?
)

data class SearchMetaVideo(
    val is_end: Boolean,
    val pageable_count: Int,
    val total_count: Int
)
data class SearchDocumentVideo(
    val title:String,
    val url:String,
    val datetime:String,
    val play_time:Int,
    val thumbnail:String,
    val author:String,
    var isLike: Boolean = false
)