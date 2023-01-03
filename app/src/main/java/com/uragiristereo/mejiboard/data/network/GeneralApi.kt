package com.uragiristereo.mejiboard.data.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Streaming
import retrofit2.http.Url

interface GeneralApi {
    @Streaming
    @GET
    suspend fun downloadFile(@Url url: String): ResponseBody

    @HEAD
    suspend fun checkFile(@Url url: String): Response<Void>
}
