# XPRESSO Café - Digital Ordering System

[![Build & Test Status](https://img.shields.io/badge/Build%20%26%20Test-Passing-brightgreen.svg)]()
[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg)]()
[![Android SDK](https://img.shields.io/badge/SDK-34-orange.svg)]()
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A complete, modern, error-free 3-tier digital ordering system for **XPRESSO Café** (`com.example.xpressocafe`), built as a Work-Integrated Learning (WIL) Portfolio of Evidence (POE) submission for **XISD6329**.

---

## 👥 Project Team & Submission Context

- **Module:** XISD6329 - Work-Integrated Learning Portfolio of Evidence (POE)
- **Project Group Members:**
  - Kieran Tyler Gordon
  - Darren Skippers
  - Riezwaan Jordan
- **Target Institution & Location:** South Africa (Currency: South African Rand `R`, e.g., `R38.00`)
- **GitHub Repository:** [https://github.com/skippy1275/XPRESSOCafe](https://github.com/skippy1275/XPRESSOCafe)

---

## 🏗️ System Architecture

The XPRESSO Café system utilizes a robust 3-Tier Client-Server Architecture:

```
+-------------------------------------------------------------+
|                 TIER 1: ANDROID FRONTEND                    |
|  Kotlin 1.9.22 | Material Design 3 | SharedPreferences      |
+-------------------------------------------------------------+
                              |
                     HTTP / JSON REST API
                              v
+-------------------------------------------------------------+
|                 TIER 2: PHP BACKEND REST API                |
|  PHP 8+ (PDO Prepared Statements, BCRYPT Hashing, CORS)     |
+-------------------------------------------------------------+
                              |
                        PDO Connection
                              v
+-------------------------------------------------------------+
|                 TIER 3: DATABASE STORAGE                    |
|  MySQL / MariaDB (xpresso_cafe) on XAMPP Stack              |
+-------------------------------------------------------------+
```

1. **Tier 1 (Presentation):** Native Android Application written in Kotlin using Material Design 3 XML layouts (`Theme.Material3.DayNight.NoActionBar`). Manages UI state, category filter chips, dynamic cart calculation, and local session storage.
2. **Tier 2 (Application/API):** PHP 8+ REST API scripts using PDO prepared statements to defend against SQL injection, BCRYPT password hashing, and standard JSON envelope responses.
3. **Tier 3 (Data Persistence):** MySQL / MariaDB database `xpresso_cafe` featuring normalized relational tables (`users`, `categories`, `products`, `orders`, `order_items`) with foreign key constraints.

---

## 📁 Repository Structure

```
XPRESSOCafe/
├── app/                                  # Android Application Module
│   ├── build.gradle.kts                  # App dependencies and SDK configuration
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml       # Manifest declaring permissions & activities
│   │   │   ├── java/com/example/xpressocafe/
│   │   │   │   ├── adapters/             # RecyclerView Adapters (Menu, Cart, Orders)
│   │   │   │   ├── api/                  # Centralized ApiConfig Network Constants
│   │   │   │   ├── models/               # Data Models (User, MenuItem, CartItem, Order, ApiResponse)
│   │   │   │   ├── network/              # Asynchronous NetworkHelper (HttpURLConnection)
│   │   │   │   ├── utils/                # Pure ValidationUtils, JsonParser, CartManager, SessionManager
│   │   │   │   ├── LoginActivity.kt      # User Auth & Session Entry
│   │   │   │   ├── RegisterActivity.kt   # New Account Creation
│   │   │   │   ├── MainActivity.kt       # Interactive Cafe Menu & Ordering
│   │   │   │   ├── CartBottomSheetDialogFragment.kt # Modal Cart Checkout
│   │   │   │   ├── OrderHistoryActivity.kt # Past Order Verification
│   │   │   │   └── SettingsActivity.kt   # Profile & Password Update
│   │   │   └── res/                      # Material 3 Layouts, Colors, Themes & Drawables
│   │   └── test/java/com/example/xpressocafe/
│   │       ├── ValidationUtilsTest.kt   # JUnit 4 Input Validation Unit Tests
│   │       ├── JsonParserTest.kt        # JUnit 4 JSON Payload Parsing Unit Tests
│   │       └── CartManagerTest.kt       # JUnit 4 Cart Calculation & Currency Format Tests
│   └── proguard-rules.pro
├── backend/
│   └── xpresso_api/                      # Full-Stack PHP Backend API Scripts
│       ├── config.php                    # DB connection configuration entry
│       ├── db_config.php                 # PDO MySQL Database connection & JSON helper
│       ├── register.php                  # User Registration Endpoint (POST)
│       ├── login.php                     # User Login Authentication Endpoint (POST)
│       ├── get_menu.php                  # Menu & Categories Retrieval Endpoint (GET)
│       ├── submit_order.php              # Transactional Order Placement Endpoint (POST)
│       ├── get_orders.php                # Order History Endpoint (GET)
│       └── update_settings.php           # Profile & Password Update Endpoint (POST)
├── database/
│   └── xpresso_cafe.sql                  # MySQL Schema DDL & South African Rand Seed Data DML
├── build.gradle.kts                      # Root Gradle build script
├── settings.gradle.kts                   # Settings and module declarations
├── gradle.properties                     # JVM and build properties
├── gradlew / gradlew.bat                 # Gradle wrapper scripts
├── .gitignore                            # Clean Git exclusion rules
├── LICENSE                               # Official MIT License
└── README.md                             # Comprehensive POE Documentation
```

---

## 🛠️ Step-by-Step Setup & Installation Guide

### Prerequisites
- **Android Studio:** Jellyfish / 2023.3.1+ (or compatible version)
- **Local Web Server:** XAMPP with Apache and MySQL modules enabled
- **Git & Java:** JDK 17 (Targeting Java 1.8 compatibility)

### 1. Database Setup (MySQL / XAMPP)
1. Launch **XAMPP Control Panel** and click **Start** for Apache and MySQL.
2. Open your browser and navigate to phpMyAdmin: [http://localhost/phpmyadmin/](http://localhost/phpmyadmin/).
3. Click the **Import** tab.
4. Choose the `database/xpresso_cafe.sql` file from this repository and click **Go**.
5. Verify that the `xpresso_cafe` database is created with tables: `users`, `categories`, `products`, `orders`, and `order_items`.

### 2. Backend API Deployment
1. Copy the `backend/xpresso_api/` folder into your XAMPP `htdocs` directory:
   `C:\xampp\htdocs\xpresso_api\`
2. Test the API in your browser by visiting:
   [http://localhost/xpresso_api/get_menu.php](http://localhost/xpresso_api/get_menu.php)
   *(It should return a JSON response containing `status: "success"` and the list of menu products).*

### 3. Centralized Network IP Configuration (`ApiConfig.kt`)
Open `app/src/main/java/com/example/xpressocafe/api/ApiConfig.kt` to configure network connectivity:

```kotlin
object ApiConfig {
    // Android Studio Emulator (Default host loopback address)
    const val BASE_URL = "http://10.0.2.2/xpresso_api/"

    // Physical Android Device over LAN Wi-Fi (Replace with your local IPv4)
    // const val BASE_URL = "http://192.168.1.100/xpresso_api/"
}
```

- **Android Studio Emulator:** Keep `10.0.2.2`. Android OS maps `10.0.2.2` to `127.0.0.1` (localhost) of your development PC.
- **Physical Android Device:** Connect your phone and PC to the same Wi-Fi network. Find your PC's IP address (`ipconfig` on Windows) and set `BASE_URL = "http://<YOUR_LOCAL_IP>/xpresso_api/"`.

---

## 📡 REST API Reference

All backend API responses enforce `Content-Type: application/json; charset=UTF-8` and conform to a standard response structure:

```json
{
  "status": "success",
  "message": "Operation completed successfully",
  "data": null
}
```

### API Endpoints Summary

| Endpoint | Method | Input Parameters / Body | Sample Response Payload |
|---|---|---|---|
| `register.php` | `POST` | `name`, `email`, `password` | `{ "status": "success", "message": "Account created successfully!" }` |
| `login.php` | `POST` | `email`, `password` | `{ "status": "success", "message": "Login successful", "data": { "user_id": 1, "name": "Test User", "email": "test@xpresso.co.za" } }` |
| `get_menu.php` | `GET` | *(None)* | `{ "status": "success", "message": "Menu loaded", "data": [ { "product_id": 3, "productName": "Cappuccino", "category": "Hot Coffee", "price": 38.00 } ] }` |
| `submit_order.php` | `POST` | JSON: `{ "user_id": 1, "total_amount": 76.00, "items": [...] }` | `{ "status": "success", "message": "Order placed successfully", "data": { "order_id": 101 } }` |
| `get_orders.php` | `GET` | Query param: `?user_id=1` | `{ "status": "success", "message": "Orders retrieved", "data": [ { "order_id": 101, "total_amount": 76.00, "status": "Completed", "items": [...] } ] }` |
| `update_settings.php` | `POST` | `email`, `new_name`, `new_password` | `{ "status": "success", "message": "Profile updated successfully!" }` |

---

## 🧪 Automated Unit Testing & Build Execution

The project features a decoupled business logic architecture allowing pure unit testing without Android OS or HttpURLConnection dependencies.

### Running Unit Tests
Execute all unit tests using the Gradle wrapper:

```powershell
./gradlew testDebugUnitTest
```

#### Unit Test Test Suite Coverage:
1. **`ValidationUtilsTest`:** Validates email regex patterns, password minimum length requirements, name formats, and registration/login form validation.
2. **`JsonParserTest`:** Verifies JSON parsing for login responses, registration responses, product menu lists, transactional order submission payloads, order history parsing, and error payload handling.
3. **`CartManagerTest`:** Tests adding items, updating item quantities, removing items, total price calculations, clearing cart state, and South African Rand currency string formatting (`R38.00`).

### Compiling Debug APK
Compile and assemble the debug application package:

```powershell
./gradlew assembleDebug
```

### Full Clean & Verification Gate
Run the complete clean, test, and build suite:

```powershell
./gradlew clean testDebugUnitTest assembleDebug
```

---

## 📜 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

*Copyright (c) 2026 Kieran Tyler Gordon, Darren Skippers, Riezwaan Jordan.*
