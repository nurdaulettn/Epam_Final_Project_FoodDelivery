-- ==================================
-- ENUMS
-- ==================================

CREATE TYPE user_role AS ENUM (
    'CUSTOMER',
    'OWNER',
    'ADMIN'
);

CREATE TYPE order_status AS ENUM (
    'PENDING',
    'CONFIRMED',
    'PREPARING',
    'READY',
    'DELIVERING',
    'COMPLETED',
    'CANCELLED'
);

CREATE TYPE delivery_type_enum AS ENUM (
    'DELIVERY',
    'PICKUP'
);

-- ==================================
-- USERS
-- ==================================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role user_role NOT NULL DEFAULT 'CUSTOMER',
    status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==================================
-- RESTAURANTS
-- ==================================

CREATE TABLE restaurants (
     id BIGSERIAL PRIMARY KEY,
     name VARCHAR(255) NOT NULL,
     description TEXT,
     address VARCHAR(500) NOT NULL,
     phone VARCHAR(30),
     rating_avg DECIMAL(3,2) DEFAULT 0,
     rating_count INTEGER DEFAULT 0,
     opening_time TIME,
     closing_time TIME,
     owner_id BIGINT NOT NULL,
     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

     CONSTRAINT fk_restaurant_owner
         FOREIGN KEY (owner_id)
             REFERENCES users(id)
             ON DELETE CASCADE
);

-- ==================================
-- CATEGORIES
-- ==================================

CREATE TABLE categories (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL UNIQUE,
                        image VARCHAR(500)
);

-- ==================================
-- FOODS
-- ==================================

CREATE TABLE foods (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       description TEXT,
                       price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
                       is_available BOOLEAN DEFAULT TRUE,
                       restaurant_id BIGINT NOT NULL,
                       category_id BIGINT NOT NULL,
                       image VARCHAR(500),

                       CONSTRAINT fk_food_restaurant
                           FOREIGN KEY (restaurant_id)
                               REFERENCES restaurants(id)
                               ON DELETE CASCADE,

                       CONSTRAINT fk_food_category
                           FOREIGN KEY (category_id)
                               REFERENCES categories(id)
                               ON DELETE RESTRICT
);

-- ==================================
-- ORDERS
-- ==================================

CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        restaurant_id BIGINT NOT NULL,
                        status order_status NOT NULL DEFAULT 'PENDING',
                        delivery_type delivery_type_enum NOT NULL,
                        delivery_address VARCHAR(500),
                        total_price DECIMAL(10,2) NOT NULL DEFAULT 0,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT fk_order_user
                            FOREIGN KEY (user_id)
                                REFERENCES users(id)
                                ON DELETE CASCADE,

                        CONSTRAINT fk_order_restaurant
                            FOREIGN KEY (restaurant_id)
                                REFERENCES restaurants(id)
                                ON DELETE CASCADE
);

-- ==================================
-- ORDER ITEMS
-- ==================================

CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             food_id BIGINT NOT NULL,
                             quantity INTEGER NOT NULL CHECK (quantity > 0),
                             price DECIMAL(10,2) NOT NULL CHECK (price >= 0),

                             CONSTRAINT fk_order_item_order
                                 FOREIGN KEY (order_id)
                                     REFERENCES orders(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_order_item_food
                                 FOREIGN KEY (food_id)
                                     REFERENCES foods(id)
                                     ON DELETE RESTRICT
);

-- ==================================
-- REVIEWS
-- ==================================

CREATE TABLE reviews (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         restaurant_id BIGINT NOT NULL,
                         rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
                         comment TEXT,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT fk_review_user
                             FOREIGN KEY (user_id)
                                 REFERENCES users(id)
                                 ON DELETE CASCADE,

                         CONSTRAINT fk_review_restaurant
                             FOREIGN KEY (restaurant_id)
                                 REFERENCES restaurants(id)
                                 ON DELETE CASCADE,

                         CONSTRAINT unique_review
                             UNIQUE (user_id, restaurant_id)
);

-- ==================================
-- FAVORITE FOODS
-- ==================================

CREATE TABLE favorite_foods (
                                id BIGSERIAL PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                food_id BIGINT NOT NULL,

                                CONSTRAINT fk_favorite_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES users(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT fk_favorite_food
                                    FOREIGN KEY (food_id)
                                        REFERENCES foods(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT unique_favorite_food
                                    UNIQUE (user_id, food_id)
);

-- ==================================
-- INDEXES
-- ==================================

CREATE INDEX idx_foods_restaurant
    ON foods(restaurant_id);

CREATE INDEX idx_foods_category
    ON foods(category_id);

CREATE INDEX idx_orders_user
    ON orders(user_id);

CREATE INDEX idx_orders_restaurant
    ON orders(restaurant_id);

CREATE INDEX idx_reviews_restaurant
    ON reviews(restaurant_id);

CREATE INDEX idx_favorite_foods_user
    ON favorite_foods(user_id);