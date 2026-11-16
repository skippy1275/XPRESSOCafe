package com.example.xpressocafe.api

/**
 * Centralized API Network Configuration for XPRESSO Cafe.
 *
 * NOTE FOR EMULATOR VS PHYSICAL DEVICE:
 * - When running in the Android Studio Emulator, use "10.0.2.2" to refer to localhost on your PC host machine.
 * - When running on a physical Android device over local Wi-Fi, change "10.0.2.2" to your PC's local LAN IP (e.g. "192.168.1.100").
 */
object ApiConfig {
    // Change IP here if running on physical device via LAN (e.g., "http://192.168.1.100/xpresso_api/")
    const val BASE_URL = "http://10.0.2.2/xpresso_api/"

    const val REGISTER_URL = "${BASE_URL}register.php"
    const val LOGIN_URL = "${BASE_URL}login.php"
    const val GET_MENU_URL = "${BASE_URL}get_menu.php"
    const val SUBMIT_ORDER_URL = "${BASE_URL}submit_order.php"
    const val GET_ORDERS_URL = "${BASE_URL}get_orders.php"
    const val UPDATE_SETTINGS_URL = "${BASE_URL}update_settings.php"
}
