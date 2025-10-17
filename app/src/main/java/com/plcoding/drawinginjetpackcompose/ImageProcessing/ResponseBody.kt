package com.plcoding.drawinginjetpackcompose.ImageProcessing

data class UploadResponse(
    val totalFiles: Int,
    val successfulUploads: Int,
    val failedUploads: Int,
    val results: List<Result>,
    val errors: List<String>
) {
    data class Result(
        val url: String,
        val filename: String,
        val size: Int,
        val contentType: String,
        val message: String,
        val documentUrl: String,
        val documentGenerated: Boolean
    )
}