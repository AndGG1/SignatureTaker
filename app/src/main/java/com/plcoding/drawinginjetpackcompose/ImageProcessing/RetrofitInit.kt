package com.plcoding.drawinginjetpackcompose.ImageProcessing

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

suspend fun useRetrofit(convertedImg: File): Boolean {
    val BASE_URL = "URL"

    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
    return retrofit.create(ImageService::class.java)
        .sendImg(image = convertedImg).success
}