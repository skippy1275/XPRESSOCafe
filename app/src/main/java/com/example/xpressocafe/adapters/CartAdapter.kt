package com.example.xpressocafe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.xpressocafe.R
import com.example.xpressocafe.models.CartItem
import com.example.xpressocafe.utils.CartManager
import com.google.android.material.button.MaterialButton

/**
 * RecyclerView Adapter for displaying items inside the shopping cart.
 */
class CartAdapter(
    private var cartItems: List<CartItem>,
    private val onItemChanged: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCartItemName: TextView = itemView.findViewById(R.id.tvCartItemName)
        val tvCartItemUnitPrice: TextView = itemView.findViewById(R.id.tvCartItemUnitPrice)
        val tvCartItemQty: TextView = itemView.findViewById(R.id.tvCartItemQty)
        val tvCartItemSubtotal: TextView = itemView.findViewById(R.id.tvCartItemSubtotal)
        val btnCartMinus: MaterialButton = itemView.findViewById(R.id.btnCartMinus)
        val btnCartPlus: MaterialButton = itemView.findViewById(R.id.btnCartPlus)
        val btnRemoveCartItem: ImageButton = itemView.findViewById(R.id.btnRemoveCartItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]

        holder.tvCartItemName.text = cartItem.menuItem.productName
        holder.tvCartItemUnitPrice.text = "${CartManager.formatPrice(cartItem.menuItem.price)} each"
        holder.tvCartItemQty.text = cartItem.quantity.toString()
        holder.tvCartItemSubtotal.text = CartManager.formatPrice(cartItem.subtotal)

        holder.btnCartPlus.setOnClickListener {
            CartManager.updateQuantity(cartItem.menuItem.productId, cartItem.quantity + 1)
            updateCartData()
        }

        holder.btnCartMinus.setOnClickListener {
            CartManager.updateQuantity(cartItem.menuItem.productId, cartItem.quantity - 1)
            updateCartData()
        }

        holder.btnRemoveCartItem.setOnClickListener {
            CartManager.removeItem(cartItem.menuItem.productId)
            updateCartData()
        }
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateCartData() {
        this.cartItems = CartManager.getItems()
        notifyDataSetChanged()
        onItemChanged()
    }
}
