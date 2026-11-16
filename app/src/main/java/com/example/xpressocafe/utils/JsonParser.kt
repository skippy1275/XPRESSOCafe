package com.example.xpressocafe.utils

import com.example.xpressocafe.models.ApiResponse
import com.example.xpressocafe.models.CartItem
import com.example.xpressocafe.models.MenuItem
import com.example.xpressocafe.models.Order
import com.example.xpressocafe.models.OrderItem
import com.example.xpressocafe.models.User
import org.json.JSONArray
import org.json.JSONObject

/**
 * Pure JSON Parsing & Formatting Utilities for XPRESSO Cafe API.
 * Decoupled from network/UI for unit testing.
 */
object JsonParser {

    /**
     * Parses login response.
     * Expects format: { "status": "success", "message": "...", "data": { "user_id": 1, "name": "...", "email": "..." } }
     */
    fun parseLoginResponse(jsonString: String): ApiResponse<User> {
        return try {
            val root = JSONObject(jsonString)
            val status = root.optString("status", "error")
            val message = root.optString("message", "")

            var user: User? = null
            if (root.has("data") && !root.isNull("data")) {
                val data = root.getJSONObject("data")
                val userId = data.optInt("user_id", data.optInt("id", -1))
                val name = data.optString("name", "")
                val email = data.optString("email", "")
                user = User(userId = userId, name = name, email = email)
            }
            ApiResponse(status = status, message = message, data = user)
        } catch (e: Exception) {
            ApiResponse(status = "error", message = "Invalid JSON response: ${e.message}", data = null)
        }
    }

    /**
     * Parses standard status/message response (e.g., register, update_settings).
     */
    fun parseSimpleResponse(jsonString: String): ApiResponse<Unit> {
        return try {
            val root = JSONObject(jsonString)
            val status = root.optString("status", "error")
            val message = root.optString("message", "")
            ApiResponse(status = status, message = message, data = if (status == "success") Unit else null)
        } catch (e: Exception) {
            ApiResponse(status = "error", message = "Invalid JSON response: ${e.message}", data = null)
        }
    }

    /**
     * Parses menu items response.
     * Expects format: { "status": "success", "message": "...", "data": [ { "product_id": 1, "productName": "...", "category": "...", "description": "...", "price": 38.00 } ] }
     */
    fun parseMenuResponse(jsonString: String): ApiResponse<List<MenuItem>> {
        return try {
            val root = JSONObject(jsonString)
            val status = root.optString("status", "error")
            val message = root.optString("message", "")

            val menuItems = mutableListOf<MenuItem>()
            if (root.has("data") && !root.isNull("data")) {
                val dataArray = root.getJSONArray("data")
                for (i in 0 until dataArray.length()) {
                    val obj = dataArray.getJSONObject(i)
                    val productId = obj.optInt("product_id", obj.optInt("id", 0))
                    val productName = obj.optString("productName", obj.optString("name", "Unknown Item"))
                    val category = obj.optString("category", "General")
                    val description = obj.optString("description", "")
                    val price = obj.optDouble("price", 0.0)

                    menuItems.add(
                        MenuItem(
                            productId = productId,
                            productName = productName,
                            category = category,
                            description = description,
                            price = price
                        )
                    )
                }
            }
            ApiResponse(status = status, message = message, data = menuItems)
        } catch (e: Exception) {
            ApiResponse(status = "error", message = "Failed to parse menu: ${e.message}", data = emptyList())
        }
    }

    /**
     * Parses order placement response.
     * Expects format: { "status": "success", "message": "...", "data": { "order_id": 101 } }
     */
    fun parseSubmitOrderResponse(jsonString: String): ApiResponse<Int> {
        return try {
            val root = JSONObject(jsonString)
            val status = root.optString("status", "error")
            val message = root.optString("message", "")

            var orderId = -1
            if (root.has("data") && !root.isNull("data")) {
                val dataObj = root.getJSONObject("data")
                orderId = dataObj.optInt("order_id", -1)
            }
            ApiResponse(status = status, message = message, data = if (orderId != -1) orderId else null)
        } catch (e: Exception) {
            ApiResponse(status = "error", message = "Invalid submit order response: ${e.message}", data = null)
        }
    }

    /**
     * Parses order history response.
     * Expects format: { "status": "success", "message": "...", "data": [ { "order_id": 101, "total_amount": 76.00, "status": "Completed", "created_at": "...", "items": [...] } ] }
     */
    fun parseOrdersResponse(jsonString: String): ApiResponse<List<Order>> {
        return try {
            val root = JSONObject(jsonString)
            val status = root.optString("status", "error")
            val message = root.optString("message", "")

            val ordersList = mutableListOf<Order>()
            if (root.has("data") && !root.isNull("data")) {
                val dataArray = root.getJSONArray("data")
                for (i in 0 until dataArray.length()) {
                    val orderObj = dataArray.getJSONObject(i)
                    val orderId = orderObj.optInt("order_id", 0)
                    val userId = orderObj.optInt("user_id", 0)
                    val totalAmount = orderObj.optDouble("total_amount", 0.0)
                    val orderStatus = orderObj.optString("status", "Pending")
                    val createdAt = orderObj.optString("created_at", "")

                    val itemsList = mutableListOf<OrderItem>()
                    if (orderObj.has("items") && !orderObj.isNull("items")) {
                        val itemsArray = orderObj.getJSONArray("items")
                        for (j in 0 until itemsArray.length()) {
                            val itemObj = itemsArray.getJSONObject(j)
                            val orderItemId = itemObj.optInt("order_item_id", 0)
                            val productId = itemObj.optInt("product_id", 0)
                            val productName = itemObj.optString("productName", itemObj.optString("name", "Product"))
                            val quantity = itemObj.optInt("quantity", 1)
                            val unitPrice = itemObj.optDouble("unit_price", 0.0)
                            val subtotal = itemObj.optDouble("subtotal", unitPrice * quantity)

                            itemsList.add(
                                OrderItem(
                                    orderItemId = orderItemId,
                                    productId = productId,
                                    productName = productName,
                                    quantity = quantity,
                                    unitPrice = unitPrice,
                                    subtotal = subtotal
                                )
                            )
                        }
                    }

                    ordersList.add(
                        Order(
                            orderId = orderId,
                            userId = userId,
                            totalAmount = totalAmount,
                            status = orderStatus,
                            createdAt = createdAt,
                            items = itemsList
                        )
                    )
                }
            }
            ApiResponse(status = status, message = message, data = ordersList)
        } catch (e: Exception) {
            ApiResponse(status = "error", message = "Failed to parse orders: ${e.message}", data = emptyList())
        }
    }

    /**
     * Creates JSON String payload for submitting an order to submit_order.php.
     */
    fun createSubmitOrderJsonPayload(userId: Int, totalAmount: Double, cartItems: List<CartItem>): String {
        val root = JSONObject()
        root.put("user_id", userId)
        root.put("total_amount", totalAmount)

        val itemsArray = JSONArray()
        for (item in cartItems) {
            val itemObj = JSONObject()
            itemObj.put("product_id", item.menuItem.productId)
            itemObj.put("quantity", item.quantity)
            itemObj.put("unit_price", item.menuItem.price)
            itemsArray.put(itemObj)
        }
        root.put("items", itemsArray)
        return root.toString()
    }
}
