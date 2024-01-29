package com.example.imagesave.retrofit

import com.example.imagesave.Contract
import com.example.imagesave.data.SearchResponse
import com.example.imagesave.data.SearchResponseVideo
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.QueryMap

interface NetWorkInterface {
    @GET(Contract.REQUEST_ADDRESS)
    suspend fun getImageResults(
        @Header("Authorization") authHeader: String,
        @QueryMap param: HashMap<String, String>
    ): SearchResponse
    @GET(Contract.REQUEST_ADDRESS_VIDEO)
    suspend fun getVideoResults(
        @Header("Authorization") authHeader: String,
        @QueryMap param: HashMap<String, String>
    ): SearchResponseVideo
}