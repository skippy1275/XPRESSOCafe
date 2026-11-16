package com.example.xpressocafe.api

/**
 * Centralized API Network Configuration for XPRESSO Cafe.
 *
 * ACTIVE NETWORK SETTING:
 * - Physical Device LAN Wi-Fi IP: "http://10.0.0.122/xpresso_api/"
 * - Android Emulator Loopback IP: "http://10.0.2.2/xpresso_api/"
 */
object ApiConfig {
    // Active host IP for connected physical device over local Wi-Fi LAN
    const val BASE_URL = "http://10.0.0.122/xpresso_api/"

    const val REGISTER_URL = "${BASE_URL}register.php"
    const val LOGIN_URL = "${BASE_URL}login.php"
    const val GET_MENU_URL = "${BASE_URL}get_menu.php"
    const val SUBMIT_ORDER_URL = "${BASE_URL}submit_order.php"
    const val GET_ORDERS_URL = "${BASE_URL}get_orders.php"
    const val UPDATE_SETTINGS_URL = "${BASE_URL}update_settings.php"
}
