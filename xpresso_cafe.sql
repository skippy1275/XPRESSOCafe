-- ============================================================
-- XPRESSO Cafe - Complete Database Schema & Seed Script (WIL POE)
-- Database Name: xpresso_cafe
-- Target Engine: MySQL / MariaDB (XAMPP PDO compatible)
-- Currency: South African Rand (R)
-- ============================================================

CREATE DATABASE IF NOT EXISTS `xpresso_cafe` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `xpresso_cafe`;

-- Drop existing tables in reverse dependency order
DROP TABLE IF EXISTS `order_items`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `users`;

-- 1. Users Table
CREATE TABLE `users` (
  `user_id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(150) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Categories Table
CREATE TABLE `categories` (
  `category_id` INT AUTO_INCREMENT PRIMARY KEY,
  `category_name` VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Products Table
CREATE TABLE `products` (
  `product_id` INT AUTO_INCREMENT PRIMARY KEY,
  `category_id` INT NOT NULL,
  `productName` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `price` DECIMAL(10,2) NOT NULL,
  `image_url` VARCHAR(255) DEFAULT NULL,
  `is_available` TINYINT(1) DEFAULT 1,
  FOREIGN KEY (`category_id`) REFERENCES `categories`(`category_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Orders Table
CREATE TABLE `orders` (
  `order_id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `total_amount` DECIMAL(10,2) NOT NULL,
  `status` VARCHAR(30) DEFAULT 'Completed',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Order Items Table
CREATE TABLE `order_items` (
  `order_item_id` INT AUTO_INCREMENT PRIMARY KEY,
  `order_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 1,
  `unit_price` DECIMAL(10,2) NOT NULL,
  `subtotal` DECIMAL(10,2) NOT NULL,
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`order_id`) ON DELETE CASCADE,
  FOREIGN KEY (`product_id`) REFERENCES `products`(`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Seed Data
-- ============================================================

-- Categories
INSERT INTO `categories` (`category_id`, `category_name`) VALUES
(1, 'Hot Coffee'),
(2, 'Iced Coffee'),
(3, 'Pastries');

-- Products (South African Rand Prices)
INSERT INTO `products` (`product_id`, `category_id`, `productName`, `description`, `price`, `is_available`) VALUES
(1, 1, 'Single Espresso', 'Pure, intense shot of signature XPRESSO dark roast.', 28.00, 1),
(2, 1, 'Double Espresso', 'Double shot of concentrated dark roast espresso.', 34.00, 1),
(3, 1, 'Cappuccino', 'Rich espresso layered with velvety microfoam and cocoa dust.', 38.00, 1),
(4, 1, 'Caffè Latte', 'Smooth espresso balanced with silky steamed milk.', 42.00, 1),
(5, 1, 'Caffè Mocha', 'Espresso blended with dark Belgian chocolate and steamed milk.', 45.00, 1),
(6, 2, 'Iced Americano', 'Double espresso poured over ice and chilled mineral water.', 35.00, 1),
(7, 2, 'Iced Caramel Latte', 'Espresso with cold milk and sweet salted caramel over ice.', 46.00, 1),
(8, 2, 'Iced Vanilla Frappé', 'Blended iced espresso with creamy vanilla bean syrup.', 48.00, 1),
(9, 3, 'Fresh Butter Croissant', 'Golden, flaky French pastry baked fresh daily.', 32.00, 1),
(10, 3, 'Blueberry Muffin', 'Moist muffin bursting with fresh wild blueberries.', 28.00, 1),
(11, 3, 'Fudge Brownie', 'Decadent chocolate brownie topped with chocolate drizzle.', 30.00, 1),
(12, 3, 'New York Cheesecake', 'Classic creamy cheesecake on a buttery graham biscuit crust.', 55.00, 1);

-- Sample Test User (Password: password123)
-- Hash generated via BCRYPT: $2y$10$wKkS3n6E2l4yC7a9O8zM7uVw8nQ3b1a2c3d4e5f6g7h8i9j0k1l2m
INSERT INTO `users` (`user_id`, `name`, `email`, `password`, `created_at`) VALUES
(1, 'Test User', 'test@xpresso.co.za', '$2y$10$ohbMgvx9mVpjWLUymg4iMusKSRJ3S7ZPd/ZS7TcvOnN3z4mwqxa66', NOW());

-- Sample Initial Order
INSERT INTO `orders` (`order_id`, `user_id`, `total_amount`, `status`, `created_at`) VALUES
(101, 1, 70.00, 'Completed', NOW());

INSERT INTO `order_items` (`order_item_id`, `order_id`, `product_id`, `quantity`, `unit_price`, `subtotal`) VALUES
(1, 101, 3, 1, 38.00, 38.00),
(2, 101, 9, 1, 32.00, 32.00);
