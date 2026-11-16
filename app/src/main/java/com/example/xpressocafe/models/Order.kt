package com.example.xpressocafe.models

data class Order(
    val orderId: Int,
    val userId: Int,
    val totalAmount: Double,
    val status: String,
    val createdAt: String,
    val items: List<OrderItem>
)
