package com.example.xpressocafe

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem as OptionMenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.xpressocafe.adapters.MenuItemAdapter
import com.example.xpressocafe.api.ApiConfig
import com.example.xpressocafe.models.ApiResponse
import com.example.xpressocafe.models.MenuItem
import com.example.xpressocafe.network.NetworkHelper
import com.example.xpressocafe.utils.CartManager
import com.example.xpressocafe.utils.JsonParser
import com.example.xpressocafe.utils.SessionManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 * Main Interactive Menu & Ordering Screen for XPRESSO Cafe.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var toolbarMain: MaterialToolbar
    private lateinit var tvWelcomeUser: TextView
    private lateinit var chipGroupCategories: ChipGroup
    private lateinit var chipAll: Chip
    private lateinit var chipHotCoffee: Chip
    private lateinit var chipIcedCoffee: Chip
    private lateinit var chipPastries: Chip
    private lateinit var swipeRefreshMenu: SwipeRefreshLayout
    private lateinit var rvMenuProducts: RecyclerView
    private lateinit var progressBarMenu: ProgressBar
    private lateinit var tvEmptyMenu: TextView

    private lateinit var cardBottomCart: MaterialCardView
    private lateinit var tvCartBadgeCount: TextView
    private lateinit var tvCartTotalPrice: TextView
    private lateinit var btnViewCart: MaterialButton

    private lateinit var sessionManager: SessionManager
    private lateinit var menuAdapter: MenuItemAdapter
    private var allMenuItems: List<MenuItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        toolbarMain = findViewById(R.id.toolbarMain)
        setSupportActionBar(toolbarMain)

        tvWelcomeUser = findViewById(R.id.tvWelcomeUser)
        chipGroupCategories = findViewById(R.id.chipGroupCategories)
        chipAll = findViewById(R.id.chipAll)
        chipHotCoffee = findViewById(R.id.chipHotCoffee)
        chipIcedCoffee = findViewById(R.id.chipIcedCoffee)
        chipPastries = findViewById(R.id.chipPastries)
        swipeRefreshMenu = findViewById(R.id.swipeRefreshMenu)
        rvMenuProducts = findViewById(R.id.rvMenuProducts)
        progressBarMenu = findViewById(R.id.progressBarMenu)
        tvEmptyMenu = findViewById(R.id.tvEmptyMenu)

        cardBottomCart = findViewById(R.id.cardBottomCart)
        tvCartBadgeCount = findViewById(R.id.tvCartBadgeCount)
        tvCartTotalPrice = findViewById(R.id.tvCartTotalPrice)
        btnViewCart = findViewById(R.id.btnViewCart)

        val userName = sessionManager.getUserName()
        tvWelcomeUser.text = if (userName.isNotEmpty()) "Welcome, $userName!" else "Welcome to XPRESSO Café!"

        rvMenuProducts.layoutManager = LinearLayoutManager(this)
        menuAdapter = MenuItemAdapter(emptyList()) {
            updateCartBarUI()
        }
        rvMenuProducts.adapter = menuAdapter

        swipeRefreshMenu.setOnRefreshListener {
            fetchMenuData()
        }

        setupCategoryFilters()

        val openCartAction = View.OnClickListener { showCartBottomSheet() }
        cardBottomCart.setOnClickListener(openCartAction)
        btnViewCart.setOnClickListener(openCartAction)

        fetchMenuData()
    }

    override fun onResume() {
        super.onResume()
        val userName = sessionManager.getUserName()
        tvWelcomeUser.text = if (userName.isNotEmpty()) "Welcome, $userName!" else "Welcome to XPRESSO Café!"
        updateCartBarUI()
    }

    private fun setupCategoryFilters() {
        chipGroupCategories.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            val selectedCategory = when (checkedIds[0]) {
                R.id.chipHotCoffee -> "Hot Coffee"
                R.id.chipIcedCoffee -> "Iced Coffee"
                R.id.chipPastries -> "Pastries"
                else -> "All"
            }
            filterMenuItems(selectedCategory)
        }
    }

    private fun filterMenuItems(category: String) {
        val filtered = if (category == "All") {
            allMenuItems
        } else {
            allMenuItems.filter { it.category.contains(category, ignoreCase = true) }
        }
        menuAdapter.updateData(filtered)
        tvEmptyMenu.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun fetchMenuData() {
        progressBarMenu.visibility = View.VISIBLE
        tvEmptyMenu.visibility = View.GONE

        NetworkHelper.sendGetRequest(
            urlString = ApiConfig.GET_MENU_URL,
            callback = object : NetworkHelper.NetworkCallback {
                override fun onSuccess(responseString: String) {
                    if (isFinishing || isDestroyed) return
                    progressBarMenu.visibility = View.GONE
                    swipeRefreshMenu.isRefreshing = false

                    val response: ApiResponse<List<MenuItem>> = JsonParser.parseMenuResponse(responseString)
                    if (response.isSuccess && response.data != null) {
                        allMenuItems = response.data
                        filterMenuItems("All")
                    } else {
                        tvEmptyMenu.text = response.message.ifEmpty { "Failed to load menu." }
                        tvEmptyMenu.visibility = View.VISIBLE
                    }
                }

                override fun onError(errorMessage: String) {
                    if (isFinishing || isDestroyed) return
                    progressBarMenu.visibility = View.GONE
                    swipeRefreshMenu.isRefreshing = false
                    tvEmptyMenu.text = errorMessage
                    tvEmptyMenu.visibility = View.VISIBLE
                }
            }
        )
    }

    private fun updateCartBarUI() {
        val count = CartManager.getItemCount()
        if (count > 0) {
            cardBottomCart.visibility = View.VISIBLE
            tvCartBadgeCount.text = getString(R.string.label_items_count, count)
            tvCartTotalPrice.text = CartManager.formatPrice(CartManager.getTotalAmount())
        } else {
            cardBottomCart.visibility = View.GONE
        }
    }

    private fun showCartBottomSheet() {
        val bottomSheet = CartBottomSheetDialogFragment.newInstance()
        bottomSheet.onOrderSubmittedListener = {
            updateCartBarUI()
            Toast.makeText(this, "Your order has been recorded!", Toast.LENGTH_SHORT).show()
        }
        bottomSheet.show(supportFragmentManager, "CartBottomSheet")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: OptionMenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                showCartBottomSheet()
                true
            }
            R.id.action_orders -> {
                startActivity(Intent(this, OrderHistoryActivity::class.java))
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_logout -> {
                sessionManager.logout()
                CartManager.clearCart()
                Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
