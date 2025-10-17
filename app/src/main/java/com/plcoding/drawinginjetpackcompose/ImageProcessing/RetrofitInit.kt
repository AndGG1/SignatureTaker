package com.plcoding.drawinginjetpackcompose.ImageProcessing

import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

suspend fun useRetrofit(convertedImages: List<MultipartBody.Part>): UploadResponse {
    val BASE_URL = "https://semnaturi-hwcfa8febvaxfkcp.westeurope-01.azurewebsites.net/"

    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    return retrofit.create(ImageService::class.java)
        .sendImg(images = convertedImages)
}
