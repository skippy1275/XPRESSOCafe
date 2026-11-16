package com.example.xpressocafe

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.xpressocafe.adapters.OrderHistoryAdapter
import com.example.xpressocafe.api.ApiConfig
import com.example.xpressocafe.models.ApiResponse
import com.example.xpressocafe.models.Order
import com.example.xpressocafe.network.NetworkHelper
import com.example.xpressocafe.utils.JsonParser
import com.example.xpressocafe.utils.SessionManager
import com.google.android.material.appbar.MaterialToolbar

/**
 * Activity for displaying user's past order history.
 */
class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var toolbarOrderHistory: MaterialToolbar
    private lateinit var swipeRefreshOrders: SwipeRefreshLayout
    private lateinit var rvOrderHistory: RecyclerView
    private lateinit var progressBarOrders: ProgressBar
    private lateinit var tvEmptyOrders: TextView

    private lateinit var sessionManager: SessionManager
    private lateinit var orderAdapter: OrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        sessionManager = SessionManager(this)

        toolbarOrderHistory = findViewById(R.id.toolbarOrderHistory)
        toolbarOrderHistory.setNavigationOnClickListener {
            finish()
        }

        swipeRefreshOrders = findViewById(R.id.swipeRefreshOrders)
        rvOrderHistory = findViewById(R.id.rvOrderHistory)
        progressBarOrders = findViewById(R.id.progressBarOrders)
        tvEmptyOrders = findViewById(R.id.tvEmptyOrders)

        rvOrderHistory.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderHistoryAdapter(emptyList())
        rvOrderHistory.adapter = orderAdapter

        swipeRefreshOrders.setOnRefreshListener {
            fetchOrders()
        }

        fetchOrders()
    }

    private fun fetchOrders() {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            tvEmptyOrders.text = getString(R.string.orders_empty)
            tvEmptyOrders.visibility = View.VISIBLE
            return
        }

        progressBarOrders.visibility = View.VISIBLE
        tvEmptyOrders.visibility = View.GONE

        val requestUrl = "${ApiConfig.GET_ORDERS_URL}?user_id=$userId"

        NetworkHelper.sendGetRequest(
            urlString = requestUrl,
            callback = object : NetworkHelper.NetworkCallback {
                override fun onSuccess(responseString: String) {
                    if (isFinishing || isDestroyed) return
                    progressBarOrders.visibility = View.GONE
                    swipeRefreshOrders.isRefreshing = false

                    val response: ApiResponse<List<Order>> = JsonParser.parseOrdersResponse(responseString)
                    if (response.isSuccess && response.data != null) {
                        val ordersList = response.data
                        if (ordersList.isEmpty()) {
                            tvEmptyOrders.visibility = View.VISIBLE
                        } else {
                            orderAdapter.updateOrders(ordersList)
                            tvEmptyOrders.visibility = View.GONE
                        }
                    } else {
                        tvEmptyOrders.text = response.message.ifEmpty { "No past orders found." }
                        tvEmptyOrders.visibility = View.VISIBLE
                    }
                }

                override fun onError(errorMessage: String) {
                    if (isFinishing || isDestroyed) return
                    progressBarOrders.visibility = View.GONE
                    swipeRefreshOrders.isRefreshing = false
                    tvEmptyOrders.text = errorMessage
                    tvEmptyOrders.visibility = View.VISIBLE
                }
            }
        )
    }
}
