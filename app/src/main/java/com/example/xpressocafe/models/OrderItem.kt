package com.example.xpressocafe.models

data class OrderItem(
    val orderItemId: Int,
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double
)
