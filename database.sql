CREATE DATABASE IF NOT EXISTS healthfirst_pims;
USE healthfirst_pims;

DROP TABLE IF EXISTS sale_items;
DROP TABLE IF EXISTS sales;
DROP TABLE IF EXISTS medicines;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
 user_id INT PRIMARY KEY AUTO_INCREMENT,
 username VARCHAR(50) UNIQUE NOT NULL,
 password VARCHAR(255) NOT NULL,
 role ENUM('Admin','Cashier') NOT NULL,
 full_name VARCHAR(100) NOT NULL
);

CREATE TABLE suppliers (
 supplier_id INT PRIMARY KEY AUTO_INCREMENT,
 name VARCHAR(100) NOT NULL,
 contact_person VARCHAR(100),
 phone VARCHAR(20),
 email VARCHAR(100),
 address TEXT
);

CREATE TABLE medicines (
 medicine_id INT PRIMARY KEY AUTO_INCREMENT,
 name VARCHAR(150) NOT NULL,
 company VARCHAR(100),
 medicine_type VARCHAR(50),
 price DECIMAL(10,2) NOT NULL,
 quantity_in_stock INT NOT NULL DEFAULT 0,
 reorder_level INT NOT NULL DEFAULT 10,
 expiry_date DATE NOT NULL,
 supplier_id INT,
 FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE sales (
 sale_id INT PRIMARY KEY AUTO_INCREMENT,
 sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 total_amount DECIMAL(10,2) NOT NULL,
 user_id INT,
 FOREIGN KEY (user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE sale_items (
 sale_item_id INT PRIMARY KEY AUTO_INCREMENT,
 sale_id INT NOT NULL,
 medicine_id INT NOT NULL,
 quantity_sold INT NOT NULL,
 price_at_sale DECIMAL(10,2) NOT NULL,
 FOREIGN KEY (sale_id) REFERENCES sales(sale_id) ON DELETE CASCADE,
 FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id) ON DELETE RESTRICT
);

INSERT INTO users(username,password,role,full_name) VALUES
('admin','admin123','Admin','System Administrator'),
('cashier','cash123','Cashier','Main Cashier');

INSERT INTO suppliers(name,contact_person,phone,email,address) VALUES
('MediSupply','John Smith','0111234567','john@medisupply.co.za','Johannesburg'),
('Pharma Distributors','Sarah Naidoo','0317654321','sarah@pharmadist.co.za','Durban'),
('Health Wholesale','David Khan','0215551234','david@healthwholesale.co.za','Cape Town');

INSERT INTO medicines(name,company,medicine_type,price,quantity_in_stock,reorder_level,expiry_date,supplier_id) VALUES
('Panado','Adcock Ingram','Tablet',35.99,50,10,'2027-06-30',1),
('Myprodol','Adcock Ingram','Capsule',79.99,25,8,'2027-03-31',1),
('Benylin','Janssen','Syrup',89.50,15,10,'2026-12-15',2),
('Betadine','Kenvue','Cream',65.00,5,10,'2026-10-20',3),
('Voltaren','Novartis','Gel',99.99,30,8,'2027-01-31',2);
