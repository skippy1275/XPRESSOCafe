package com.example.xpressocafe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.xpressocafe.R
import com.example.xpressocafe.models.Order
import com.example.xpressocafe.utils.CartManager

/**
 * RecyclerView Adapter for displaying past orders in OrderHistoryActivity.
 */
class OrderHistoryAdapter(
    private var orders: List<Order>
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        val tvOrderStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
        val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        val tvOrderItemsSummary: TextView = itemView.findViewById(R.id.tvOrderItemsSummary)
        val tvOrderTotalAmount: TextView = itemView.findViewById(R.id.tvOrderTotalAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.tvOrderId.text = "Order #${order.orderId}"
        holder.tvOrderStatus.text = order.status
        holder.tvOrderDate.text = order.createdAt
        holder.tvOrderTotalAmount.text = CartManager.formatPrice(order.totalAmount)

        val summaryBuilder = StringBuilder()
        if (order.items.isNotEmpty()) {
            for (item in order.items) {
                summaryBuilder.append("• ${item.quantity}x ${item.productName} (${CartManager.formatPrice(item.subtotal)})\n")
            }
            holder.tvOrderItemsSummary.text = summaryBuilder.toString().trimEnd()
        } else {
            holder.tvOrderItemsSummary.text = "Item details unavailable"
        }
    }

    override fun getItemCount(): Int = orders.size

    fun updateOrders(newOrders: List<Order>) {
        this.orders = newOrders
        notifyDataSetChanged()
    }
}
