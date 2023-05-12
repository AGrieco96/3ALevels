package com.A3Levels.other

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {

    @POST("$URL/new-game")
    suspend fun createGame(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("$URL/ai")
    suspend fun photoAI(@Body requestBody: RequestBody): Response<ResponseBody>

}