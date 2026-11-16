package com.example.xpressocafe.models

data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T? = null
) {
    val isSuccess: Boolean
        get() = status.lowercase() == "success"
}
