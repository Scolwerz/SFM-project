USE autoszerviz_db;

-- 1. Autómárkák
CREATE TABLE IF NOT EXISTS vehicle_brands (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    origin_region VARCHAR(100) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- 2. Típusok/Modellek (motorral és üzemanyagtípussal együtt)
CREATE TABLE IF NOT EXISTS vehicle_models (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    engine_type VARCHAR(80) NULL,
    fuel_type VARCHAR(50) NOT NULL DEFAULT 'PETROL',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_models_brand FOREIGN KEY (brand_id) 
        REFERENCES vehicle_brands(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 3. Ügyfelek járművei
CREATE TABLE IF NOT EXISTS vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    brand_id BIGINT NOT NULL,
    model_id BIGINT NOT NULL,
    year SMALLINT NOT NULL,
    license_plate VARCHAR(20) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicles_owner FOREIGN KEY (owner_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_vehicles_brand FOREIGN KEY (brand_id) 
        REFERENCES vehicle_brands(id) ON DELETE RESTRICT,
    CONSTRAINT fk_vehicles_model FOREIGN KEY (model_id) 
        REFERENCES vehicle_models(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE INDEX idx_vehicles_owner ON vehicles(owner_id);