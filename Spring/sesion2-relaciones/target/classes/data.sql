-- ============================================================
-- DATOS INICIALES - Sesion 2
-- ============================================================

-- Customers
INSERT INTO customers (name, email, address) VALUES
('Ana Garcia',   'ana@email.com',    'Av. Lima 123'),
('Carlos Perez', 'carlos@email.com', 'Jr. Cusco 456'),
('Maria Torres', 'maria@email.com',  'Calle Miraflores 789'),
('Juan Lopez',   'juan@email.com',   'Av. Larco 321');

-- Products
INSERT INTO products (name, price, stock, category) VALUES
('Laptop Dell XPS 15',  4500.00, 10, 'ELECTRONICS'),
('Mouse Logitech MX3',   150.00, 50, 'ELECTRONICS'),
('Teclado Mecanico',     250.00, 30, 'ELECTRONICS'),
('Polo Casual Azul',      45.00, 100,'CLOTHING'),
('Jeans Slim Fit',        89.00, 80, 'CLOTHING'),
('Zapatillas Running',   199.00, 60, 'SPORTS'),
('Pelota Futbol',         55.00, 40, 'SPORTS'),
('Arroz Premium 5kg',     25.00, 200,'FOOD'),
('Monitor 4K 27 pulgadas',1200.00,15,'ELECTRONICS');

-- Orders (customer_id referencia a customers)
INSERT INTO orders (status, total, created_at, customer_id) VALUES
('PENDING',   4650.00, NOW() - INTERVAL '2 days',  1),
('PAID',       289.00, NOW() - INTERVAL '5 days',  1),
('SHIPPED',    199.00, NOW() - INTERVAL '10 days', 2),
('PENDING',   1445.00, NOW() - INTERVAL '1 day',   3),
('DELIVERED',   45.00, NOW() - INTERVAL '20 days', 2),
('CANCELLED',  199.00, NOW() - INTERVAL '40 days', 4);

-- OrderItems (order_id y product_id referencian a sus tablas)
-- Pedido 1 (Ana, PENDING): Laptop + Mouse
INSERT INTO order_items (quantity, unit_price, order_id, product_id) VALUES
(1, 4500.00, 1, 1),
(1,  150.00, 1, 2);

-- Pedido 2 (Ana, PAID): Teclado + Mouse
INSERT INTO order_items (quantity, unit_price, order_id, product_id) VALUES
(1, 250.00, 2, 3),
(1, 150.00, 2, 2);

-- Pedido 3 (Carlos, SHIPPED): Zapatillas
INSERT INTO order_items (quantity, unit_price, order_id, product_id) VALUES
(1, 199.00, 3, 6);

-- Pedido 4 (Maria, PENDING): Monitor + Mouse
INSERT INTO order_items (quantity, unit_price, order_id, product_id) VALUES
(1, 1200.00, 4, 9),
(1,  150.00, 4, 2),
(1,   95.00, 4, 4);

-- Pedido 5 (Carlos, DELIVERED): Polo
INSERT INTO order_items (quantity, unit_price, order_id, product_id) VALUES
(1, 45.00, 5, 4);

-- Pedido 6 (Juan, CANCELLED): Zapatillas
INSERT INTO order_items (quantity, unit_price, order_id, product_id) VALUES
(1, 199.00, 6, 6);
