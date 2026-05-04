-- =====================================================
-- COMPLETE DATABASE SETUP (Run this if starting over)
-- =====================================================

-- Drop tables if they exist (order matters due to foreign keys)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS sales_record;
DROP TABLE IF EXISTS product_recipes;
DROP TABLE IF EXISTS product_menu;
DROP TABLE IF EXISTS product_categories;
DROP TABLE IF EXISTS product_stocks;
DROP TABLE IF EXISTS product_unit;
DROP TABLE IF EXISTS supplier;
DROP TABLE IF EXISTS user_data;
DROP TABLE IF EXISTS job_position;
SET FOREIGN_KEY_CHECKS = 1;

-- Create job_position table
CREATE TABLE job_position (
    job_id INT PRIMARY KEY AUTO_INCREMENT,
    job_roles VARCHAR(55) UNIQUE NOT NULL
);

-- Create user_data table
CREATE TABLE user_data (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(40) NOT NULL,
    last_name VARCHAR(40) NOT NULL,
    birthdate DATE,
    sex VARCHAR(5),
    password VARCHAR(200) NOT NULL,
    roles INT,
    FOREIGN KEY (roles) REFERENCES job_position(job_id) ON DELETE SET NULL
);

-- ADD USERNAME COLUMN
ALTER TABLE user_data ADD user_name VARCHAR(80);

-- Create product_unit table
CREATE TABLE product_unit (
    unit_id INT PRIMARY KEY AUTO_INCREMENT,
    unit_type VARCHAR(100) UNIQUE NOT NULL
);

-- Create supplier table
CREATE TABLE supplier (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    supplier_name VARCHAR(100) UNIQUE NOT NULL,
    supply_type VARCHAR(100) NOT NULL,
    contact_info VARCHAR(50),
    email VARCHAR(100)
);

-- Create product_stocks table
CREATE TABLE product_stocks (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    item_name VARCHAR(100) UNIQUE NOT NULL,
    total_unit_count INT DEFAULT 0,
    minimum_unit_count INT DEFAULT 0,
    unit_type INT,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    supplier_id INT,
    FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id) ON DELETE SET NULL,
    FOREIGN KEY (unit_type) REFERENCES product_unit(unit_id) ON DELETE SET NULL
);

-- Create product_categories table
CREATE TABLE product_categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(40) UNIQUE NOT NULL
);

-- Create product_menu table
CREATE TABLE product_menu (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100) UNIQUE NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES product_categories(category_id) ON DELETE SET NULL
);

-- Create product_recipes table
CREATE TABLE product_recipes (
    recipe_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    ingredients_need INT,
    amount_needed DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product_menu(product_id) ON DELETE CASCADE,
    FOREIGN KEY (ingredients_need) REFERENCES product_stocks(item_id) ON DELETE CASCADE
);

-- Create sales_record table
CREATE TABLE sales_record (
    order_number INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100),
    quantity INT,
    total_price DECIMAL(10, 2),
    date_purchased TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES user_data(user_id) ON DELETE SET NULL
);

-- =====================================================
-- INSERT INITIAL DATA
-- =====================================================

-- Insert job positions
INSERT INTO job_position (job_roles) VALUES ('Admin');
INSERT INTO job_position (job_roles) VALUES ('Sales');
INSERT INTO job_position (job_roles) VALUES ('Inventory Manager');

-- Insert product units
INSERT INTO product_unit (unit_type) VALUES ('grams');
INSERT INTO product_unit (unit_type) VALUES ('milliliters');
INSERT INTO product_unit (unit_type) VALUES ('pieces');

-- Insert suppliers
INSERT INTO supplier (supplier_name, supply_type, contact_info, email) VALUES 
('nestle', 'dairy products', '09123456789', 'nestle@gmail.com'),
('nescafe', 'coffee products', '0155215151', 'nescafe@gmail.com'),
('King Flour', 'baking products', '696969', 'flour@gmail.com'),
('styro', 'coffee cups', '236454541', 'styro@gmail.com'),
('sugar cane', 'sugar products', '055565229', 'sugar@gmail.com');

-- Insert product stocks
INSERT INTO product_stocks (item_name, total_unit_count, minimum_unit_count, unit_type, supplier_id) VALUES 
('milk', 36000, 5000, 2, 1),
('coffee beans', 36000, 5000, 3, 2),
('small cups', 400, 250, 1, 4),
('sugar', 4000, 250, 3, 5),
('corn flour', 4000, 250, 3, 3),
('large cups', 4000, 250, 1, 4);

-- Insert product categories
INSERT INTO product_categories (category_name) VALUES 
('Hot Beverage'),
('Bread and Pastry'),
('Cold Beverage');

-- Insert product menu
INSERT INTO product_menu (product_name, price, category_id) VALUES 
('Coffee Regular Small', 45.20, 1),
('Iced Coffee Small', 67.69, 3),
('Banana Bread', 15.34, 2),
('Large Coffee', 15.34, 1);

-- Insert product recipes (assuming product_id 4 = Large Coffee)
INSERT INTO product_recipes (product_id, ingredients_need, amount_needed) VALUES 
(4, 2, 30),  -- coffee beans
(4, 1, 50),  -- milk
(4, 4, 35),  -- sugar
(4, 6, 1);   -- large cups

-- Insert sample users (for testing)
INSERT INTO user_data (first_name, last_name, birthdate, sex, password, roles, user_name) VALUES 
('John', 'Doe', '1987-11-06', 'M', 'admin123', 1, 'john@admin.com'),
('Jane', 'Smith', '1990-05-15', 'F', 'cashier123', 2, 'jane@cashier.com'),
('Mike', 'Johnson', '1985-03-20', 'M', 'inv123', 3, 'mike@inventory.com');

-- =====================================================
-- VERIFY ALL DATA
-- =====================================================
SELECT 'job_position' as Table_Name, COUNT(*) as Record_Count FROM job_position
UNION ALL
SELECT 'user_data', COUNT(*) FROM user_data
UNION ALL
SELECT 'product_menu', COUNT(*) FROM product_menu
UNION ALL
SELECT 'product_stocks', COUNT(*) FROM product_stocks
UNION ALL
SELECT 'product_recipes', COUNT(*) FROM product_recipes;
