package com.example.xpressocafe.utils

import com.example.xpressocafe.models.CartItem
import com.example.xpressocafe.models.MenuItem
import java.util.Locale

/**
 * Singleton Cart Manager for managing shopping cart operations and price formatting.
 */
object CartManager {

    private val cartItems = mutableMapOf<Int, CartItem>()

    /**
     * Adds an item to the cart or increments its quantity.
     */
    fun addItem(menuItem: MenuItem, quantityToAdd: Int = 1) {
        val existing = cartItems[menuItem.productId]
        if (existing != null) {
            existing.quantity += quantityToAdd
        } else {
            cartItems[menuItem.productId] = CartItem(menuItem = menuItem, quantity = quantityToAdd)
        }
    }

    /**
     * Updates quantity of a product in the cart.
     * If quantity <= 0, item is removed from cart.
     */
    fun updateQuantity(productId: Int, quantity: Int) {
        if (quantity <= 0) {
            cartItems.remove(productId)
        } else {
            cartItems[productId]?.quantity = quantity
        }
    }

    /**
     * Removes an item completely from the cart.
     */
    fun removeItem(productId: Int) {
        cartItems.remove(productId)
    }

    /**
     * Gets all current cart items as a List.
     */
    fun getItems(): List<CartItem> {
        return cartItems.values.toList()
    }

    /**
     * Gets quantity of a specific product in the cart.
     */
    fun getQuantityForProduct(productId: Int): Int {
        return cartItems[productId]?.quantity ?: 0
    }

    /**
     * Returns total count of all items in cart.
     */
    fun getItemCount(): Int {
        return cartItems.values.sumOf { it.quantity }
    }

    /**
     * Returns total cart sum.
     */
    fun getTotalAmount(): Double {
        return cartItems.values.sumOf { it.subtotal }
    }

    /**
     * Clears all items from cart.
     */
    fun clearCart() {
        cartItems.clear()
    }

    /**
     * Formats price value into South African Rand currency string (e.g. R38.00).
     */
    fun formatPrice(price: Double): String {
        return String.format(Locale.US, "R%.2f", price)
    }
}
