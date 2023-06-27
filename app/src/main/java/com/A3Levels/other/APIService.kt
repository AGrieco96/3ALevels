package com.A3Levels.other

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface APIService {

    @POST("$URL/new-game")
    suspend fun createGame(@Body requestBody: RequestBody): Response<ResponseBody>

    @PUT("$URL/update-game")
    suspend fun updateGame(@Body requestBody: RequestBody): Response<ResponseBody>

    @PUT("$URL/ai")
    suspend fun photoAI(@Body requestBody: RequestBody): Response<ResponseBody>

}