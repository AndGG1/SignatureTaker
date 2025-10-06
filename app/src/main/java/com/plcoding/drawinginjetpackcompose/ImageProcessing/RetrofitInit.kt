package com.plcoding.drawinginjetpackcompose.ImageProcessing

import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

suspend fun useRetrofit(convertedImg: MultipartBody.Part): Boolean {
    val BASE_URL = "https://postman-echo.com/"

    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    return retrofit.create(ImageService::class.java)
        .sendImg(image = convertedImg).success
}
