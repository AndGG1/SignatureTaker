package com.plcoding.drawinginjetpackcompose.ImageProcessing

data class ResponseBody(
    val url: String,
    val filename: String,
    val size: Int,
    val contentType: String,
    val message: String,
    val documentUrl: String,
    val documentGenerated: Boolean
)