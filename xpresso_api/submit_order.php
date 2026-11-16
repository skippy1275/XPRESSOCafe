<?php
/**
 * Submit Order API Endpoint for XPRESSO Cafe.
 * Accepts POST (JSON or form data) with user_id, total_amount, items array.
 * Performs database transaction to insert into orders and order_items tables.
 */
require_once __DIR__ . '/db_config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendJsonResponse("error", "Invalid request method. POST required.", null, 405);
}

$rawInput = file_get_contents('php://input');
$inputData = json_decode($rawInput, true);

if (!is_array($inputData)) {
    $inputData = $_POST;
    if (isset($inputData['items']) && is_string($inputData['items'])) {
        $inputData['items'] = json_decode($inputData['items'], true);
    }
}

$userId = (int)($inputData['user_id'] ?? 0);
$totalAmount = (float)($inputData['total_amount'] ?? 0.0);
$items = $inputData['items'] ?? [];

if ($userId <= 0) {
    sendJsonResponse("error", "Invalid or missing user_id.");
}

if (!is_array($items) || count($items) === 0) {
    sendJsonResponse("error", "Order must contain at least one item.");
}

try {
    $pdo->beginTransaction();

    // Insert order record
    $orderStmt = $pdo->prepare("INSERT INTO orders (user_id, total_amount, status, created_at) VALUES (?, ?, 'Completed', NOW())");
    $orderStmt->execute([$userId, $totalAmount]);
    $orderId = (int)$pdo->lastInsertId();

    // Insert order item records
    $itemStmt = $pdo->prepare("INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)");

    foreach ($items as $item) {
        $productId = (int)($item['product_id'] ?? 0);
        $quantity = (int)($item['quantity'] ?? 1);
        $unitPrice = (float)($item['unit_price'] ?? 0.0);
        $subtotal = $quantity * $unitPrice;

        if ($productId > 0 && $quantity > 0) {
            $itemStmt->execute([$orderId, $productId, $quantity, $unitPrice, $subtotal]);
        }
    }

    $pdo->commit();

    sendJsonResponse("success", "Order placed successfully", ["order_id" => $orderId]);
} catch (Exception $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    sendJsonResponse("error", "Failed to process order: " . $e->getMessage());
}
