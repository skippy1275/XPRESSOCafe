<?php
/**
 * Login API Endpoint for XPRESSO Cafe.
 * Accepts POST parameters: email, password
 */
require_once __DIR__ . '/db_config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendJsonResponse("error", "Invalid request method. POST required.", null, 405);
}

$email = trim($_POST['email'] ?? '');
$password = trim($_POST['password'] ?? '');

// Handle raw JSON input fallback
if (empty($email) && empty($password)) {
    $rawInput = file_get_contents('php://input');
    $jsonData = json_decode($rawInput, true);
    if (is_array($jsonData)) {
        $email = trim($jsonData['email'] ?? '');
        $password = trim($jsonData['password'] ?? '');
    }
}

if (empty($email) || empty($password)) {
    sendJsonResponse("error", "Please provide email and password.");
}

try {
    $stmt = $pdo->prepare("SELECT user_id, name, email, password FROM users WHERE email = ?");
    $stmt->execute([$email]);
    $user = $stmt->fetch();

    if (!$user) {
        sendJsonResponse("error", "Invalid email or password.");
    }

    if (!password_verify($password, $user['password'])) {
        sendJsonResponse("error", "Invalid email or password.");
    }

    $userData = [
        "user_id" => (int)$user['user_id'],
        "name" => $user['name'],
        "email" => $user['email']
    ];

    sendJsonResponse("success", "Login successful", $userData);
} catch (PDOException $e) {
    sendJsonResponse("error", "Database error: " . $e->getMessage());
}
