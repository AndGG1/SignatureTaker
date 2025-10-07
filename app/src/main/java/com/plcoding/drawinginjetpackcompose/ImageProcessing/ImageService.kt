package com.plcoding.drawinginjetpackcompose.ImageProcessing

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageService {
    @Multipart
    @POST("api/files/upload")
    suspend fun sendImg(
        @Part image: MultipartBody.Part
    ) : ResponseBody
}
