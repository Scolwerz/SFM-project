USE autoszerviz_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(191) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'MECHANIC', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS mechanic_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    display_name VARCHAR(120) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    max_parallel_jobs INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_mechanic_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO users (id, full_name, email, password_hash, role, active)
VALUES 
(1, 'Teszt Ügyfél', 'ugyfel@auto.hu', 'ugyfel', 'CUSTOMER', TRUE),
(2, 'Autószerelő Admin', 'admin', 'admin', 'MECHANIC', TRUE)
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name);