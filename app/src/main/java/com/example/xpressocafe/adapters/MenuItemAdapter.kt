package com.example.xpressocafe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.xpressocafe.R
import com.example.xpressocafe.models.MenuItem
import com.example.xpressocafe.utils.CartManager
import com.google.android.material.button.MaterialButton

/**
 * RecyclerView Adapter for displaying cafe menu items with quantity controls and cart additions.
 */
class MenuItemAdapter(
    private var items: List<MenuItem>,
    private val onCartUpdated: () -> Unit
) : RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder>() {

    class MenuItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvCategoryBadge: TextView = itemView.findViewById(R.id.tvCategoryBadge)
        val tvProductDescription: TextView = itemView.findViewById(R.id.tvProductDescription)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val tvItemQty: TextView = itemView.findViewById(R.id.tvItemQty)
        val btnDecreQty: MaterialButton = itemView.findViewById(R.id.btnDecreQty)
        val btnIncreQty: MaterialButton = itemView.findViewById(R.id.btnIncreQty)
        val btnAddToCart: MaterialButton = itemView.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_product, parent, false)
        return MenuItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val menuItem = items[position]

        holder.tvProductName.text = menuItem.productName
        holder.tvCategoryBadge.text = menuItem.category
        holder.tvProductDescription.text = menuItem.description
        holder.tvProductPrice.text = CartManager.formatPrice(menuItem.price)

        var selectedQty = 1
        holder.tvItemQty.text = selectedQty.toString()

        holder.btnIncreQty.setOnClickListener {
            selectedQty++
            holder.tvItemQty.text = selectedQty.toString()
        }

        holder.btnDecreQty.setOnClickListener {
            if (selectedQty > 1) {
                selectedQty--
                holder.tvItemQty.text = selectedQty.toString()
            }
        }

        holder.btnAddToCart.setOnClickListener {
            CartManager.addItem(menuItem, selectedQty)
            selectedQty = 1
            holder.tvItemQty.text = "1"
            onCartUpdated()
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<MenuItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
