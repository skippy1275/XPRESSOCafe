<?php
/**
 * Settings / Profile Update API Endpoint for XPRESSO Cafe.
 * Accepts POST parameters: email, new_name, new_password
 */
require_once __DIR__ . '/db_config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendJsonResponse("error", "Invalid request method. POST required.", null, 405);
}

$email = trim($_POST['email'] ?? '');
$newName = trim($_POST['new_name'] ?? '');
$newPassword = trim($_POST['new_password'] ?? '');

// Handle raw JSON input fallback
if (empty($email) && empty($newName) && empty($newPassword)) {
    $rawInput = file_get_contents('php://input');
    $jsonData = json_decode($rawInput, true);
    if (is_array($jsonData)) {
        $email = trim($jsonData['email'] ?? '');
        $newName = trim($jsonData['new_name'] ?? '');
        $newPassword = trim($jsonData['new_password'] ?? '');
    }
}

if (empty($email)) {
    sendJsonResponse("error", "Logged in user email is required.");
}

if (empty($newName) && empty($newPassword)) {
    sendJsonResponse("error", "Please provide a new name or new password to update.");
}

try {
    // Verify user exists
    $stmt = $pdo->prepare("SELECT user_id FROM users WHERE email = ?");
    $stmt->execute([$email]);
    $user = $stmt->fetch();

    if (!$user) {
        sendJsonResponse("error", "User account not found.");
    }

    $updateFields = [];
    $queryParams = [];

    if (!empty($newName)) {
        if (strlen($newName) < 2) {
            sendJsonResponse("error", "Name must be at least 2 characters.");
        }
        $updateFields[] = "name = ?";
        $queryParams[] = $newName;
    }

    if (!empty($newPassword)) {
        if (strlen($newPassword) < 6) {
            sendJsonResponse("error", "New password must be at least 6 characters.");
        }
        $updateFields[] = "password = ?";
        $queryParams[] = password_hash($newPassword, PASSWORD_BCRYPT);
    }

    $queryParams[] = $email;
    $sql = "UPDATE users SET " . implode(", ", $updateFields) . " WHERE email = ?";
    $updateStmt = $pdo->prepare($sql);
    $updateStmt->execute($queryParams);

    sendJsonResponse("success", "Profile updated successfully!");
} catch (PDOException $e) {
    sendJsonResponse("error", "Database error: " . $e->getMessage());
}
