package com.example.xpressocafe

import com.example.xpressocafe.models.MenuItem
import com.example.xpressocafe.utils.CartManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive Unit Tests for CartManager.
 */
class CartManagerTest {

    private val item1 = MenuItem(1, "Cappuccino", "Hot Coffee", "Rich espresso", 38.00)
    private val item2 = MenuItem(2, "Latte", "Hot Coffee", "Milky coffee", 42.00)

    @Before
    fun setUp() {
        CartManager.clearCart()
    }

    @Test
    fun addItem_addsNewItemAndIncrementsQuantity() {
        CartManager.addItem(item1)
        assertEquals(1, CartManager.getItemCount())
        assertEquals(38.00, CartManager.getTotalAmount(), 0.001)

        CartManager.addItem(item1)
        assertEquals(2, CartManager.getItemCount())
        assertEquals(76.00, CartManager.getTotalAmount(), 0.001)
    }

    @Test
    fun updateQuantity_modifiesOrRemovesItem() {
        CartManager.addItem(item1, 2)
        CartManager.addItem(item2, 1)

        assertEquals(3, CartManager.getItemCount())
        assertEquals(118.00, CartManager.getTotalAmount(), 0.001)

        CartManager.updateQuantity(item1.productId, 1)
        assertEquals(2, CartManager.getItemCount())
        assertEquals(80.00, CartManager.getTotalAmount(), 0.001)

        CartManager.updateQuantity(item2.productId, 0)
        assertEquals(1, CartManager.getItemCount())
        assertEquals(38.00, CartManager.getTotalAmount(), 0.001)
    }

    @Test
    fun removeItem_removesProductFromCart() {
        CartManager.addItem(item1)
        CartManager.addItem(item2)

        CartManager.removeItem(item1.productId)
        assertEquals(1, CartManager.getItemCount())
        assertEquals(42.00, CartManager.getTotalAmount(), 0.001)
        assertEquals(0, CartManager.getQuantityForProduct(item1.productId))
    }

    @Test
    fun clearCart_emptiesCart() {
        CartManager.addItem(item1, 3)
        CartManager.addItem(item2, 2)
        assertTrue(CartManager.getItemCount() > 0)

        CartManager.clearCart()
        assertEquals(0, CartManager.getItemCount())
        assertEquals(0.00, CartManager.getTotalAmount(), 0.001)
        assertTrue(CartManager.getItems().isEmpty())
    }

    @Test
    fun formatPrice_formatsSouthAfricanRand() {
        assertEquals("R38.00", CartManager.formatPrice(38.0))
        assertEquals("R0.00", CartManager.formatPrice(0.0))
        assertEquals("R123.45", CartManager.formatPrice(123.45))
    }
}
