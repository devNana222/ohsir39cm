
-- 고객 데이터
INSERT INTO customer (balance) VALUES
           (10000),
           (20000),
           (15000),
           (30000),
           (25000);

-- 제품 데이터
INSERT INTO product (product_nm, price, category) VALUES
          ('무선 이어폰', 45000, '가전'),
          ('스마트폰 케이스', 15000, '액세서리'),
          ('노트북 가방', 30000, '가전'),
          ('블루투스 스피커', 55000, '가전'),
          ('3D 프린터', 450000, '가전');

-- 제품 재고 데이터
INSERT INTO product_inventory (product_id, amount) VALUES
           (1, 100),
           (2, 150),
           (3, 200),
           (4, 250),
           (5, 300);

-- 주문 데이터
INSERT INTO `customer_order` (customer_id) VALUES
           (1),
           (2),
           (3),
           (1),
           (4);

-- 주문 제품 데이터
INSERT INTO order_product (order_id, product_id, amount, price) VALUES
            (1, 1, 2, 45000),
            (1, 2, 1, 15000),
            (2, 3, 5, 30000),
            (3, 4, 1, 55000),
            (4, 5, 3, 450000);

-- 장바구니 데이터
INSERT INTO cart (customer_id, product_id, amount) VALUES
           (1, 1, 2),
           (2, 2, 1),
           (3, 3, 5),
           (1, 4, 1),
           (4, 5, 3);