package com.example.xpressocafe.models

data class CartItem(
    val menuItem: MenuItem,
    var quantity: Int = 1
) {
    val subtotal: Double
        get() = menuItem.price * quantity
}
