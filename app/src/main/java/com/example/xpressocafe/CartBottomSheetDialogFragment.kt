package com.example.xpressocafe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xpressocafe.adapters.CartAdapter
import com.example.xpressocafe.api.ApiConfig
import com.example.xpressocafe.models.ApiResponse
import com.example.xpressocafe.network.NetworkHelper
import com.example.xpressocafe.utils.CartManager
import com.example.xpressocafe.utils.JsonParser
import com.example.xpressocafe.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

/**
 * Bottom Sheet Modal Dialog for reviewing shopping cart items and submitting cafe orders.
 */
class CartBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var rvCartItems: RecyclerView
    private lateinit var tvEmptyCart: TextView
    private lateinit var tvCartTotalAmount: TextView
    private lateinit var btnClearCart: MaterialButton
    private lateinit var btnSubmitOrder: MaterialButton
    private lateinit var progressBarOrder: ProgressBar

    private lateinit var cartAdapter: CartAdapter
    private lateinit var sessionManager: SessionManager

    var onOrderSubmittedListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        rvCartItems = view.findViewById(R.id.rvCartItems)
        tvEmptyCart = view.findViewById(R.id.tvEmptyCart)
        tvCartTotalAmount = view.findViewById(R.id.tvCartTotalAmount)
        btnClearCart = view.findViewById(R.id.btnClearCart)
        btnSubmitOrder = view.findViewById(R.id.btnSubmitOrder)
        progressBarOrder = view.findViewById(R.id.progressBarOrder)

        rvCartItems.layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter(CartManager.getItems()) {
            updateCartUI()
        }
        rvCartItems.adapter = cartAdapter

        btnClearCart.setOnClickListener {
            CartManager.clearCart()
            updateCartUI()
        }

        btnSubmitOrder.setOnClickListener {
            submitOrder()
        }

        updateCartUI()
    }

    private fun updateCartUI() {
        val items = CartManager.getItems()
        if (items.isEmpty()) {
            rvCartItems.visibility = View.GONE
            tvEmptyCart.visibility = View.VISIBLE
            btnSubmitOrder.isEnabled = false
        } else {
            rvCartItems.visibility = View.VISIBLE
            tvEmptyCart.visibility = View.GONE
            btnSubmitOrder.isEnabled = true
        }
        tvCartTotalAmount.text = CartManager.formatPrice(CartManager.getTotalAmount())
    }

    private fun submitOrder() {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            Toast.makeText(requireContext(), "Please login to place an order.", Toast.LENGTH_SHORT).show()
            return
        }

        val cartItems = CartManager.getItems()
        if (cartItems.isEmpty()) {
            Toast.makeText(requireContext(), "Your cart is empty.", Toast.LENGTH_SHORT).show()
            return
        }

        val totalAmount = CartManager.getTotalAmount()
        val jsonPayload = JsonParser.createSubmitOrderJsonPayload(userId, totalAmount, cartItems)

        progressBarOrder.visibility = View.VISIBLE
        btnSubmitOrder.isEnabled = false
        btnClearCart.isEnabled = false

        NetworkHelper.sendPostRequest(
            urlString = ApiConfig.SUBMIT_ORDER_URL,
            jsonBody = jsonPayload,
            callback = object : NetworkHelper.NetworkCallback {
                override fun onSuccess(responseString: String) {
                    if (!isAdded) return
                    progressBarOrder.visibility = View.GONE
                    btnSubmitOrder.isEnabled = true
                    btnClearCart.isEnabled = true

                    val response: ApiResponse<Int> = JsonParser.parseSubmitOrderResponse(responseString)
                    if (response.isSuccess) {
                        Toast.makeText(
                            requireContext(),
                            "Order placed successfully! Order #${response.data ?: ""}",
                            Toast.LENGTH_LONG
                        ).show()
                        CartManager.clearCart()
                        onOrderSubmittedListener?.invoke()
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Order Failed: ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onError(errorMessage: String) {
                    if (!isAdded) return
                    progressBarOrder.visibility = View.GONE
                    btnSubmitOrder.isEnabled = true
                    btnClearCart.isEnabled = true
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    companion object {
        fun newInstance(): CartBottomSheetDialogFragment {
            return CartBottomSheetDialogFragment()
        }
    }
}
