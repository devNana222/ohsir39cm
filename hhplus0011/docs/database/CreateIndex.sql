CREATE INDEX idx_product_id ON order_product (product_id);
CREATE INDEX idx_createdAt ON order_product (reg_date);
CREATE INDEX idx_createdAt_product_id ON order_product (reg_date, product_id);

CREATE INDEX idx_regDate ON ORDER_PRODUCT (reg_date);

CREATE INDEX idx_orderId ON CUSTOMER_ORDER (order_id);
CREATE INDEX idx_orderId ON ORDER_PRODUCT (order_id);

CREATE INDEX idx_product_id ON PRODUCT_INVENTORY (product_id);
ALTER TABLE PRODUCT_INVENTORY DROP INDEX idx_product_id;
CREATE INDEX idx_product_inventory_amount_product_id ON PRODUCT_INVENTORY(amount, product_id);
ALTER TABLE PRODUCT_INVENTORY DROP INDEX idx_product_inventory_amount_product_id;
