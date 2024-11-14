-- 고객 더미 데이터 생성

INSERT INTO customer (balance)
SELECT FLOOR(RAND() * 100000)
FROM dual
LIMIT 10000;




-- product 테이블에 100개의 상품 데이터 생성
INSERT INTO product (product_nm, price, category)
SELECT
    CONCAT('Product_', LPAD(seq.seq, 3, '0')),
    FLOOR(RAND() * 1000) + 1000,  -- 가격은 1000~1999 사이로 설정
    CASE WHEN RAND() < 0.5 THEN 'category1' ELSE 'category2' END
FROM
    (SELECT @rownum := @rownum + 1 AS seq FROM (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) a,
                                               (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) b,
                                               (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) c,
                                               (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) d,
                                               (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) e,
                                               (SELECT @rownum := 0) r
    ) seq
LIMIT 100;

-- product_inventory 테이블에 각 product의 inventory 데이터 생성
INSERT INTO product_inventory (product_id, amount)
SELECT
    product_id,
    FLOOR(RAND() * 50) + 1  -- 재고는 1~50 사이로 설정
FROM
    product
LIMIT 100;



-- 고객 주문 더미 데이터 생성
INSERT INTO customer_order (customer_id)
SELECT
    FLOOR(RAND() * 10000) + 1
FROM dual
LIMIT 10000;

-- 주문 상품 더미 데이터 생성
INSERT INTO order_product (order_id, product_id, amount, price)
SELECT
    FLOOR(RAND() * 10000) + 1,
    FLOOR(RAND() * 10000) + 1,
    FLOOR(RAND() * 10) + 1,
    FLOOR(RAND() * 1000) + 1000
FROM dual
LIMIT 1000000;

-- 장바구니 더미 데이터 생성
INSERT INTO cart (customer_id, product_id, amount)
SELECT
    FLOOR(RAND() * 10000) + 1,
    FLOOR(RAND() * 10000) + 1,
    FLOOR(RAND() * 5) + 1
FROM dual
LIMIT 10000;



SELECT COUNT(*)
FROM product;

SELECT MAX(product_id)
FROM product_inventory;

SELECT *
FROM product p
inner join product_inventory pi
on p.product_id = pi.product_id;


-- 1. 임시 테이블 생성 (기존에 존재하면 삭제)
DROP TABLE IF EXISTS number_generator;

CREATE TABLE number_generator (
                                  id INT PRIMARY KEY AUTO_INCREMENT
) ENGINE=Memory;

-- 2. 임시 테이블에 데이터 삽입
-- 대략 100만 개의 데이터를 만들기 위해 적절한 배수로 데이터를 생성합니다.
INSERT INTO number_generator () VALUES (),(),(),(),(),(),(),(),(),();
INSERT INTO number_generator SELECT NULL FROM number_generator;
INSERT INTO number_generator SELECT NULL FROM number_generator;
INSERT INTO number_generator SELECT NULL FROM number_generator;
INSERT INTO number_generator SELECT NULL FROM number_generator;
INSERT INTO number_generator SELECT NULL FROM number_generator LIMIT 1000000;


INSERT INTO customer_order (customer_id)
SELECT FLOOR(RAND() * 100) + 1
FROM number_generator
LIMIT 1000000;


INSERT INTO order_product (order_id, product_id, amount, price)
SELECT
    ng.id AS order_id,                      -- 임시 테이블의 id를 order_id로 사용
    FLOOR(RAND() * 100) + 1,                -- product_id는 1~100 사이의 값
    FLOOR(RAND() * 10) + 1,                 -- amount는 1~10 사이의 값
    FLOOR(RAND() * 10000) + 1000             -- price는 1000~1999 사이의 값
FROM
    number_generator ng
LIMIT 1000000;



DROP TABLE IF EXISTS number_generator;


UPDATE order_product
SET reg_date = DATE_SUB(reg_date, INTERVAL 4 DAY)
WHERE order_id % 20 = 0;



USE ecommerce;

-- 테이블 초기화
TRUNCATE TABLE order_product;
TRUNCATE TABLE customer_order;
TRUNCATE TABLE product_inventory;
TRUNCATE TABLE product;
TRUNCATE TABLE cart;
TRUNCATE TABLE customer;

-- 1. customer 테이블에 1,000명의 고객 데이터 생성
INSERT INTO customer (balance)
WITH RECURSIVE cte AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM cte WHERE n < 1000
)
SELECT FLOOR(RAND() * 10000) AS balance
FROM cte;

-- 2. product 테이블에 1,000,000개의 제품 데이터 생성
SET SESSION cte_max_recursion_depth = 10000000;


-- product 테이블에 1,000,000개의 제품 데이터 생성
INSERT INTO product (product_nm, price, category)
WITH RECURSIVE cte (n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM cte WHERE n <= 1000000
)
SELECT
    CONCAT('Product', LPAD(n, 7, '0')) AS product_nm,
    FLOOR(RAND() * 1000) AS price,
    CASE WHEN n % 4 = 0 THEN 'food'
         WHEN n % 4 = 1 THEN 'clothes'
         WHEN n % 4 = 2 THEN 'elec'
         ELSE 'etc' END AS category
FROM cte;

-- product_inventory에 각 product의 재고 데이터를 생성
INSERT INTO product_inventory (product_id, amount)
SELECT product_id, FLOOR(RAND() * 100)
FROM product;

-- customer_order에 주문 데이터 생성
INSERT INTO customer_order (customer_id)
SELECT customer_id FROM customer ORDER BY RAND() LIMIT 1000000;

-- order_product에 각 주문마다 임의의 제품 추가
INSERT INTO order_product (order_id, product_id, amount, price)
SELECT
    customer_order.order_id,
    product.product_id,
    FLOOR(RAND() * 5) + 1 AS amount,
    product.price
FROM
    customer_order
        JOIN
    product ON RAND() < 0.01;  -- 일부 제품을 임의로 추가