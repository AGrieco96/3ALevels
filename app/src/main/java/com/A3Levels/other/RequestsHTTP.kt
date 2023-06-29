package com.A3Levels.other

import android.util.Log
import com.google.gson.GsonBuilder
import io.grpc.internal.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

const val URL = "https://yellow-beers-invite.loca.lt"

class RequestsHTTP {
    companion object {

        fun httpPOSTCreateGame(jsonObject: JSONObject) {
            // Create Retrofit
            val retrofit = Retrofit.Builder().baseUrl(URL).build()

            // Create Service
            val service = retrofit.create(APIService::class.java)

            // Convert JSONObject to String
            val jsonObjectString = jsonObject.toString()

            // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

            CoroutineScope(Dispatchers.IO).launch {
                // Do the POST request and get response
                val response = service.createGame(requestBody)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        /*

                        // Convert raw JSON to pretty JSON using GSON library
                        val gson = GsonBuilder().setPrettyPrinting().create()
                        val prettyJson = gson.toJson(
                            JsonParser.parse(
                                response.body()
                                    ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                            )
                        )

                        Log.d("Pretty Printed JSON :", prettyJson)

                        */

                    } else {

                        Log.e("RETROFIT_ERROR", response.code().toString())

                    }
                }
            }
        }
        fun httpPOSTGameUpdate(jsonObject: JSONObject) {

            // Create Retrofit
            val retrofit = Retrofit.Builder().baseUrl(URL).build()

            // Create Service
            val service = retrofit.create(APIService::class.java)

            // Convert JSONObject to String
            val jsonObjectString = jsonObject.toString()

            // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

            CoroutineScope(Dispatchers.IO).launch {
                // Do the PUT request and get response
                val response = service.updateGame(requestBody)
                println(response)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        /*
                        // Convert raw JSON to pretty JSON using GSON library
                        val gson = GsonBuilder().setPrettyPrinting().create()
                        val prettyJson = gson.toJson(
                            JsonParser.parse(
                                response.body()
                                    ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                            )
                        )

                        Log.d("Pretty Printed JSON :", prettyJson)
                        */

                    } else {

                        Log.e("RETROFIT_ERROR", response.code().toString())

                    }
                }
            }
        }
        fun httpPOSTphotoAI(jsonObject: JSONObject) {
            // Create Retrofit
            val httpClient = OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).build()
            val retrofit = Retrofit.Builder().baseUrl(URL).client(httpClient).build()

            // Create Service
            val service = retrofit.create(APIService::class.java)

            // Convert JSONObject to String
            val jsonObjectString = jsonObject.toString()

            // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

            CoroutineScope(Dispatchers.IO).launch {
                // Do the POST request and get response
                val response = service.photoAI(requestBody)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        /*
                        // Convert raw JSON to pretty JSON using GSON library
                        val gson = GsonBuilder().setPrettyPrinting().create()
                        val prettyJson = gson.toJson(
                            JsonParser.parse(
                                response.body()
                                    ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                            )
                        )

                        Log.d("Pretty Printed JSON :", prettyJson)


                         */
                    } else {

                        Log.e("RETROFIT_ERROR", response.code().toString())

                    }
                }
            }
        }

        fun httpPUTsendMessage(jsonObject: JSONObject) {

            // Create Retrofit
            val retrofit = Retrofit.Builder().baseUrl(URL).build()

            // Create Service
            val service = retrofit.create(APIService::class.java)

            // Convert JSONObject to String
            val jsonObjectString = jsonObject.toString()

            // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

            CoroutineScope(Dispatchers.IO).launch {
                // Do the PUT request and get response
                val response = service.sendMessage(requestBody)
                println(response)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        /*
                        // Convert raw JSON to pretty JSON using GSON library
                        val gson = GsonBuilder().setPrettyPrinting().create()
                        val prettyJson = gson.toJson(
                            JsonParser.parse(
                                response.body()
                                    ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                            )
                        )

                        Log.d("Pretty Printed JSON :", prettyJson)
                        */

                    } else {

                        Log.e("RETROFIT_ERROR", response.code().toString())

                    }
                }
            }
        }

    }
}