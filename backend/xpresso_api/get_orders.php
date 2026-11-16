<?php
/**
 * Order History API Endpoint for XPRESSO Cafe.
 * Accepts GET query parameter: user_id
 */
require_once __DIR__ . '/db_config.php';

$userId = (int)($_GET['user_id'] ?? 0);

if ($userId <= 0) {
    sendJsonResponse("error", "Valid user_id query parameter is required.");
}

try {
    // Fetch orders for user
    $orderSql = "SELECT order_id, user_id, total_amount, status, created_at FROM orders WHERE user_id = ? ORDER BY created_at DESC";
    $stmt = $pdo->prepare($orderSql);
    $stmt->execute([$userId]);
    $orders = $stmt->fetchAll();

    $resultOrders = [];

    foreach ($orders as $orderRow) {
        $orderId = (int)$orderRow['order_id'];

        // Fetch item details for this order
        $itemSql = "SELECT oi.order_item_id, oi.product_id, p.productName, oi.quantity, oi.unit_price, oi.subtotal
                    FROM order_items oi
                    LEFT JOIN products p ON oi.product_id = p.product_id
                    WHERE oi.order_id = ?";
        $itemStmt = $pdo->prepare($itemSql);
        $itemStmt->execute([$orderId]);
        $items = $itemStmt->fetchAll();

        $formattedItems = [];
        foreach ($items as $itemRow) {
            $formattedItems[] = [
                "order_item_id" => (int)$itemRow['order_item_id'],
                "product_id" => (int)$itemRow['product_id'],
                "productName" => $itemRow['productName'] ?? "Product #{$itemRow['product_id']}",
                "quantity" => (int)$itemRow['quantity'],
                "unit_price" => (float)$itemRow['unit_price'],
                "subtotal" => (float)$itemRow['subtotal']
            ];
        }

        $resultOrders[] = [
            "order_id" => $orderId,
            "user_id" => (int)$orderRow['user_id'],
            "total_amount" => (float)$orderRow['total_amount'],
            "status" => $orderRow['status'],
            "created_at" => $orderRow['created_at'],
            "items" => $formattedItems
        ];
    }

    sendJsonResponse("success", "Orders retrieved successfully", $resultOrders);
} catch (PDOException $e) {
    sendJsonResponse("error", "Database error: " . $e->getMessage());
}
