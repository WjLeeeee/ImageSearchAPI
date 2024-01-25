package com.example.imagesave.retrofit

import com.example.imagesave.Contract
import com.example.imagesave.data.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.QueryMap

interface NetWorkInterface {
    @GET(Contract.REQUEST_ADDRESS)
    suspend fun getImage(
        @Header("Authorization") authHeader: String,
        @QueryMap param: HashMap<String, String>
    ): SearchResponse
}