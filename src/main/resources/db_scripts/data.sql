-- ==========================
-- USERS
-- ==========================

INSERT INTO users (
    first_name, last_name, username, email, password, role, status
) VALUES
      ('Admin', 'System', 'admin', 'admin@gmail.com', '$2a$10$tEpnTT4U28AJ98pk4gDrA.zd8DOgMmwb25oIBEA19fKyqBGfo8WRG', 'ADMIN', true),
      ('John', 'Owner', 'manager', 'manager@gmail.com', '$2a$10$tEpnTT4U28AJ98pk4gDrA.zd8DOgMmwb25oIBEA19fKyqBGfo8WRG', 'MANAGER', true),
      ('Alice', 'Smith', 'alice', 'alice@gmail.com', '$2a$10$tEpnTT4U28AJ98pk4gDrA.zd8DOgMmwb25oIBEA19fKyqBGfo8WRG', 'CUSTOMER', true);

-- ==========================
-- CATEGORIES
-- ==========================

INSERT INTO categories (name, image) VALUES
                                         ('Burgers', 'burgers.jpg'),
                                         ('Pizza', 'pizza.jpg'),
                                         ('Drinks', 'drinks.jpg'),
                                         ('Desserts', 'desserts.jpg');

-- ==========================
-- RESTAURANTS
-- ==========================

INSERT INTO restaurants (
    name,
    description,
    address,
    phone,
    rating_avg,
    rating_count,
    opening_time,
    closing_time,
    manager_id,
    status
) VALUES
      (
          'Burger House',
          'Best burgers in town',
          '123 Main Street',
          '+77010000001',
          4.8,
          120,
          '09:00',
          '23:00',
          2,
          'ACTIVE'
      ),
      (
          'Italian Pizza',
          'Authentic Italian pizza',
          '456 Central Avenue',
          '+77010000002',
          4.6,
          90,
          '10:00',
          '22:00',
          2,
          'ACTIVE'
      );

-- ==========================
-- FOODS
-- ==========================

INSERT INTO foods (
    name,
    description,
    price,
    is_available,
    restaurant_id,
    category_id,
    image
) VALUES
      ('Classic Burger', 'Beef burger with cheese', 3500, true, 1, 1, 'burger1.jpg'),
      ('Double Burger', 'Double beef burger', 4500, true, 1, 1, 'burger2.jpg'),
      ('Chicken Burger', 'Chicken burger', 3200, true, 1, 1, 'burger3.jpg'),

      ('Pepperoni Pizza', 'Pepperoni pizza', 5000, true, 2, 2, 'pizza1.jpg'),
      ('Margherita Pizza', 'Classic Margherita', 4500, true, 2, 2, 'pizza2.jpg'),
      ('BBQ Pizza', 'BBQ chicken pizza', 5500, true, 2, 2, 'pizza3.jpg'),

      ('Coca Cola', '0.5L', 700, true, 1, 3, 'cola.jpg'),
      ('Orange Juice', 'Fresh juice', 1200, true, 2, 3, 'juice.jpg'),

      ('Chocolate Cake', 'Chocolate dessert', 1800, true, 2, 4, 'cake.jpg'),
      ('Ice Cream', 'Vanilla ice cream', 1000, true, 1, 4, 'icecream.jpg');

-- ==========================
-- ORDERS
-- ==========================

INSERT INTO orders (
    user_id,
    restaurant_id,
    status,
    delivery_type,
    delivery_address,
    total_price
) VALUES
      (
          3,
          1,
          'COMPLETED',
          'DELIVERY',
          'Almaty, Abay Avenue 15',
          7700
      ),
      (
          3,
          2,
          'PREPARING',
          'PICKUP',
          NULL,
          6200
      );

-- ==========================
-- ORDER ITEMS
-- ==========================

INSERT INTO order_items (
    order_id,
    food_id,
    quantity,
    price
) VALUES
      (1, 1, 2, 3500),
      (1, 7, 1, 700),

      (2, 4, 1, 5000),
      (2, 8, 1, 1200);

-- ==========================
-- REVIEWS
-- ==========================

INSERT INTO reviews (
    user_id,
    restaurant_id,
    rating,
    comment
) VALUES
      (
          3,
          1,
          5,
          'Amazing burgers and fast delivery!'
      ),
      (
          3,
          2,
          4,
          'Very tasty pizza.'
      );

-- ==========================
-- FAVORITE FOODS
-- ==========================

INSERT INTO favorite_foods (
    user_id,
    food_id
) VALUES
      (3, 1),
      (3, 4),
      (3, 9);