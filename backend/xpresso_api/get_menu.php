<?php
/**
 * Menu Retrieval API Endpoint for XPRESSO Cafe.
 * Accepts GET request and returns categories and products array.
 */
require_once __DIR__ . '/db_config.php';

try {
    $sql = "SELECT p.product_id, p.productName, c.category_name AS category, p.description, p.price
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.category_id
            WHERE p.is_available = 1
            ORDER BY c.category_id ASC, p.productName ASC";

    $stmt = $pdo->query($sql);
    $products = $stmt->fetchAll();

    $formattedProducts = [];
    foreach ($products as $row) {
        $formattedProducts[] = [
            "product_id" => (int)$row['product_id'],
            "productName" => $row['productName'],
            "category" => $row['category'] ?? "General",
            "description" => $row['description'] ?? "",
            "price" => (float)$row['price']
        ];
    }

    sendJsonResponse("success", "Menu loaded", $formattedProducts);
} catch (PDOException $e) {
    sendJsonResponse("error", "Database error: " . $e->getMessage());
}
