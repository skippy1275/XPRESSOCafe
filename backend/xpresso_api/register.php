<?php
/**
 * Registration API Endpoint for XPRESSO Cafe.
 * Accepts POST parameters: name, email, password
 */
require_once __DIR__ . '/db_config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendJsonResponse("error", "Invalid request method. POST required.", null, 405);
}

$name = trim($_POST['name'] ?? '');
$email = trim($_POST['email'] ?? '');
$password = trim($_POST['password'] ?? '');

// Handle raw JSON input fallback
if (empty($name) && empty($email) && empty($password)) {
    $rawInput = file_get_contents('php://input');
    $jsonData = json_decode($rawInput, true);
    if (is_array($jsonData)) {
        $name = trim($jsonData['name'] ?? '');
        $email = trim($jsonData['email'] ?? '');
        $password = trim($jsonData['password'] ?? '');
    }
}

if (empty($name) || empty($email) || empty($password)) {
    sendJsonResponse("error", "Please provide name, email, and password.");
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    sendJsonResponse("error", "Invalid email format.");
}

if (strlen($password) < 6) {
    sendJsonResponse("error", "Password must be at least 6 characters long.");
}

try {
    // Check if email already registered
    $stmt = $pdo->prepare("SELECT user_id FROM users WHERE email = ?");
    $stmt->execute([$email]);
    if ($stmt->fetch()) {
        sendJsonResponse("error", "An account with this email already exists.");
    }

    // Hash password using BCRYPT
    $hashedPassword = password_hash($password, PASSWORD_BCRYPT);

    // Insert user into database
    $insertStmt = $pdo->prepare("INSERT INTO users (name, email, password, created_at) VALUES (?, ?, ?, NOW())");
    $insertStmt->execute([$name, $email, $hashedPassword]);

    sendJsonResponse("success", "Account created successfully!");
} catch (PDOException $e) {
    sendJsonResponse("error", "Database error: " . $e->getMessage());
}
