SELECT *
FROM product p
         INNER JOIN product_inventory PI
                    ON p.product_id = PI.product_id
WHERE PI.amount > 0;

SELECT op.product_id, SUM(op.amount)
FROM order_product op
WHERE op.reg_date >= :threeDaysAgo
GROUP BY op.product_id
ORDER BY SUM(op.amount) DESC
