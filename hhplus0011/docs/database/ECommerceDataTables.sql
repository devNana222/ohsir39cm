drop table customer;
drop table product;
drop table product_inventory;
drop table customer_order;
drop table order_product;
drop table cart;

CREATE TABLE `customer` (
    `customer_id` INT NOT NULL AUTO_INCREMENT,
    `balance` INT NOT NULL,
    `reg_date` TIMESTAMP NULL DEFAULT (CURRENT_TIMESTAMP),
    `edit_date` TIMESTAMP NULL DEFAULT (CURRENT_TIMESTAMP) ON UPDATE CURRENT_TIMESTAMP,
    'version'   INT,
    PRIMARY KEY (`customer_id`) USING BTREE
);

CREATE TABLE `product` (
   `product_id` BIGINT NOT NULL AUTO_INCREMENT,
   `product_nm` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
   `price` INT NOT NULL,
   `category` VARCHAR(25) NULL DEFAULT 'etc' COLLATE 'utf8mb4_0900_ai_ci',
   `reg_date` TIMESTAMP NULL DEFAULT (CURRENT_TIMESTAMP),
   `edit_date` TIMESTAMP NULL DEFAULT (CURRENT_TIMESTAMP) ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`product_id`) USING BTREE
);

CREATE TABLE `product_inventory` (
     `id` BIGINT NOT NULL AUTO_INCREMENT,
     `product_id` BIGINT NOT NULL DEFAULT '0',
     `amount` INT NOT NULL,
     `reg_date` TIMESTAMP NULL DEFAULT (CURRENT_TIMESTAMP),
     `edit_date` TIMESTAMP NULL DEFAULT (CURRENT_TIMESTAMP) ON UPDATE CURRENT_TIMESTAMP,
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `idx_product_inventory_amount_product_id` (`amount`, `product_id`) USING BTREE
);

CREATE TABLE `customer_order` (
  `order_id` BIGINT NOT NULL AUTO_INCREMENT,
  `customer_id` INT NOT NULL,
  `reg_date` TIMESTAMP NULL DEFAULT (CURRENT_TIMESTAMP),
  `edit_date` TIMESTAMP NULL DEFAULT (CURRENT_TIMESTAMP) ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`) USING BTREE
);

CREATE TABLE `order_product` (
 `id` BIGINT NOT NULL AUTO_INCREMENT,
 `order_id` BIGINT NOT NULL DEFAULT '0',
 `product_id` INT NOT NULL,
 `amount` INT NOT NULL,
 `price` INT NOT NULL,
 `reg_date` TIMESTAMP NULL DEFAULT (CURRENT_TIMESTAMP),
 `edit_date` TIMESTAMP NULL DEFAULT (CURRENT_TIMESTAMP) ON UPDATE CURRENT_TIMESTAMP,
 PRIMARY KEY (`id`) USING BTREE,
 INDEX `idx_produtId_createdAt` (`product_id`, `reg_date`) USING BTREE
);

CREATE TABLE `cart` (
    `cart_id` INT NOT NULL AUTO_INCREMENT,
    `customer_id` INT NOT NULL,
    `product_id` INT NOT NULL,
    `amount` INT NOT NULL DEFAULT '0',
    `reg_date` TIMESTAMP NULL DEFAULT (CURRENT_TIMESTAMP),
    `edit_date` TIMESTAMP NULL DEFAULT (CURRENT_TIMESTAMP) ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`cart_id`) USING BTREE
);

