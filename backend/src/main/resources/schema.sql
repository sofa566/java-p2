CREATE DATABASE IF NOT EXISTS b2b_management;
USE b2b_management;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    role ENUM('ADMIN', 'STORE_MANAGER', 'USER') DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    address VARCHAR(255),
    city VARCHAR(50),
    state VARCHAR(50),
    zip_code VARCHAR(10),
    country VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    manager_id BIGINT,
    status ENUM('ACTIVE', 'INACTIVE', 'PENDING') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (manager_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS store_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role ENUM('MANAGER', 'EMPLOYEE', 'VIEWER') DEFAULT 'EMPLOYEE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_store_user (store_id, user_id)
);

CREATE TABLE IF NOT EXISTS billing_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    account_name VARCHAR(100) NOT NULL,
    account_type ENUM('CREDIT', 'DEBIT', 'PREPAID') DEFAULT 'CREDIT',
    balance DECIMAL(15,2) DEFAULT 0.00,
    credit_limit DECIMAL(15,2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'USD',
    status ENUM('ACTIVE', 'SUSPENDED', 'CLOSED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    billing_account_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    tax_amount DECIMAL(15,2) DEFAULT 0.00,
    total_amount DECIMAL(15,2) NOT NULL,
    status ENUM('DRAFT', 'SENT', 'PAID', 'OVERDUE', 'CANCELLED') DEFAULT 'DRAFT',
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    paid_date DATE NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (billing_account_id) REFERENCES billing_accounts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS invoice_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity DECIMAL(10,3) DEFAULT 1.000,
    unit_price DECIMAL(12,2) NOT NULL,
    total_price DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    payment_method ENUM('CREDIT_CARD', 'BANK_TRANSFER', 'CHECK', 'CASH', 'OTHER') NOT NULL,
    payment_reference VARCHAR(100),
    payment_date DATE NOT NULL,
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE
);

-- Indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_stores_manager ON stores(manager_id);
CREATE INDEX idx_store_users_store ON store_users(store_id);
CREATE INDEX idx_store_users_user ON store_users(user_id);
CREATE INDEX idx_billing_accounts_store ON billing_accounts(store_id);
CREATE INDEX idx_invoices_billing_account ON invoices(billing_account_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoice_items_invoice ON invoice_items(invoice_id);
CREATE INDEX idx_payments_invoice ON payments(invoice_id);