package com.example.xpressocafe

import com.example.xpressocafe.models.CartItem
import com.example.xpressocafe.models.MenuItem
import com.example.xpressocafe.utils.JsonParser
import org.junit.Assert.*
import org.junit.Test

/**
 * Comprehensive Unit Tests for JsonParser.
 */
class JsonParserTest {

    @Test
    fun parseLoginResponse_successJson_parsesUserData() {
        val json = """
            {
                "status": "success",
                "message": "Login successful",
                "data": {
                    "user_id": 10,
                    "name": "Coffee Lover",
                    "email": "lover@xpresso.co.za"
                }
            }
        """.trimIndent()

        val response = JsonParser.parseLoginResponse(json)
        assertTrue(response.isSuccess)
        assertEquals("Login successful", response.message)
        assertNotNull(response.data)
        assertEquals(10, response.data?.userId)
        assertEquals("Coffee Lover", response.data?.name)
        assertEquals("lover@xpresso.co.za", response.data?.email)
    }

    @Test
    fun parseLoginResponse_errorJson_returnsErrorStatus() {
        val json = """
            {
                "status": "error",
                "message": "Invalid password"
            }
        """.trimIndent()

        val response = JsonParser.parseLoginResponse(json)
        assertFalse(response.isSuccess)
        assertEquals("Invalid password", response.message)
        assertNull(response.data)
    }

    @Test
    fun parseMenuResponse_successJson_parsesList() {
        val json = """
            {
                "status": "success",
                "message": "Menu loaded",
                "data": [
                    {
                        "product_id": 1,
                        "productName": "Cappuccino",
                        "category": "Hot Coffee",
                        "description": "Rich espresso with steamed milk foam",
                        "price": 38.00
                    },
                    {
                        "product_id": 2,
                        "productName": "Croissant",
                        "category": "Pastries",
                        "description": "Flaky butter croissant",
                        "price": 32.50
                    }
                ]
            }
        """.trimIndent()

        val response = JsonParser.parseMenuResponse(json)
        assertTrue(response.isSuccess)
        val items = response.data
        assertNotNull(items)
        assertEquals(2, items?.size)
        assertEquals("Cappuccino", items?.get(0)?.productName)
        assertEquals(38.00, items?.get(0)?.price ?: 0.0, 0.001)
        assertEquals("Croissant", items?.get(1)?.productName)
    }

    @Test
    fun parseSubmitOrderResponse_successJson_returnsOrderId() {
        val json = """
            {
                "status": "success",
                "message": "Order placed successfully",
                "data": {
                    "order_id": 105
                }
            }
        """.trimIndent()

        val response = JsonParser.parseSubmitOrderResponse(json)
        assertTrue(response.isSuccess)
        assertEquals(105, response.data)
    }

    @Test
    fun parseOrdersResponse_successJson_parsesOrdersAndItems() {
        val json = """
            {
                "status": "success",
                "message": "Orders retrieved",
                "data": [
                    {
                        "order_id": 201,
                        "user_id": 10,
                        "total_amount": 70.50,
                        "status": "Completed",
                        "created_at": "2026-03-20 14:30:00",
                        "items": [
                            {
                                "order_item_id": 1,
                                "product_id": 1,
                                "productName": "Cappuccino",
                                "quantity": 1,
                                "unit_price": 38.00,
                                "subtotal": 38.00
                            },
                            {
                                "order_item_id": 2,
                                "product_id": 2,
                                "productName": "Croissant",
                                "quantity": 1,
                                "unit_price": 32.50,
                                "subtotal": 32.50
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()

        val response = JsonParser.parseOrdersResponse(json)
        assertTrue(response.isSuccess)
        val orders = response.data
        assertNotNull(orders)
        assertEquals(1, orders?.size)
        val order = orders?.get(0)
        assertEquals(201, order?.orderId)
        assertEquals(70.50, order?.totalAmount ?: 0.0, 0.001)
        assertEquals(2, order?.items?.size)
    }

    @Test
    fun createSubmitOrderJsonPayload_generatesValidJson() {
        val item1 = MenuItem(1, "Cappuccino", "Hot Coffee", "Desc", 38.00)
        val item2 = MenuItem(2, "Croissant", "Pastries", "Desc", 32.50)
        val cartItems = listOf(CartItem(item1, 2), CartItem(item2, 1))

        val payload = JsonParser.createSubmitOrderJsonPayload(10, 108.50, cartItems)
        assertTrue(payload.contains("\"user_id\":10"))
        assertTrue(payload.contains("\"total_amount\":108.5"))
        assertTrue(payload.contains("\"product_id\":1"))
        assertTrue(payload.contains("\"quantity\":2"))
    }
}
