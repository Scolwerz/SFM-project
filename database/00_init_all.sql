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

-- Autómárkák
CREATE TABLE IF NOT EXISTS vehicle_brands (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    origin_region VARCHAR(100) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- Típusok/Modellek
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

-- Ügyfelek járművei
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

-- Autómárkák
INSERT INTO vehicle_brands (id, name, origin_region, active) VALUES
(1, 'Toyota', 'Ázsia', TRUE),
(2, 'Volkswagen', 'Európa', TRUE),
(3, 'Ford', 'Amerika', TRUE),
(4, 'BMW', 'Európa', TRUE),
(5, 'Audi', 'Európa', TRUE),
(6, 'Opel', 'Európa', TRUE),
(7, 'Skoda', 'Európa', TRUE),
(8, 'Mercedes-Benz', 'Európa', TRUE),
(9, 'Renault', 'Európa', TRUE),
(10, 'Suzuki', 'Ázsia', TRUE)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Modellek motorral és üzemanyaggal (fuel_type)
-- Toyota
INSERT INTO vehicle_models (brand_id, name, engine_type, fuel_type, active) VALUES
(1, 'Corolla', '1.8 Hybrid', 'HYBRID', TRUE),
(1, 'Corolla', '1.6 VVT-i', 'PETROL', TRUE),
(1, 'Yaris', '1.5 Hybrid', 'HYBRID', TRUE),
(1, 'RAV4', '2.5 Hybrid AWD', 'HYBRID', TRUE);

-- Volkswagen
INSERT INTO vehicle_models (brand_id, name, engine_type, fuel_type, active) VALUES
(2, 'Golf', '1.4 TSI', 'PETROL', TRUE),
(2, 'Golf', '2.0 TDI', 'DIESEL', TRUE),
(2, 'Passat', '2.0 TDI', 'DIESEL', TRUE),
(2, 'Polo', '1.0 TSI', 'PETROL', TRUE);

-- Ford
INSERT INTO vehicle_models (brand_id, name, engine_type, fuel_type, active) VALUES
(3, 'Focus', '1.0 EcoBoost', 'PETROL', TRUE),
(3, 'Focus', '1.5 EcoBlue', 'DIESEL', TRUE),
(3, 'Mondeo', '2.0 TDCi', 'DIESEL', TRUE),
(3, 'Fiesta', '1.25 Duratec', 'PETROL', TRUE);

-- BMW
INSERT INTO vehicle_models (brand_id, name, engine_type, fuel_type, active) VALUES
(4, '3 Series', '320d', 'DIESEL', TRUE),
(4, '3 Series', '330i', 'PETROL', TRUE),
(4, '5 Series', '530d', 'DIESEL', TRUE);

-- Audi
INSERT INTO vehicle_models (brand_id, name, engine_type, fuel_type, active) VALUES
(5, 'A3', '1.6 TDI', 'DIESEL', TRUE),
(5, 'A4', '2.0 TDI', 'DIESEL', TRUE),
(5, 'A6', '3.0 TDI Quattro', 'DIESEL', TRUE);

-- Opel
INSERT INTO vehicle_models (brand_id, name, engine_type, fuel_type, active) VALUES
(6, 'Astra', '1.4 Turbo', 'PETROL', TRUE),
(6, 'Astra', '1.7 CDTI', 'DIESEL', TRUE),
(6, 'Corsa', '1.2 Benzin', 'PETROL', TRUE);

-- Skoda
INSERT INTO vehicle_models (brand_id, name, engine_type, fuel_type, active) VALUES
(7, 'Octavia', '1.6 CR TDI', 'DIESEL', TRUE),
(7, 'Octavia', '2.0 TDI RS', 'DIESEL', TRUE),
(7, 'Fabia', '1.2 TSI', 'PETROL', TRUE);

-- Mercedes-Benz
INSERT INTO vehicle_models (brand_id, name, engine_type, fuel_type, active) VALUES
(8, 'C-Class', 'C 220 d', 'DIESEL', TRUE),
(8, 'E-Class', 'E 350 CDI', 'DIESEL', TRUE);

-- Renault
INSERT INTO vehicle_models (brand_id, name, engine_type, fuel_type, active) VALUES
(9, 'Clio', '0.9 TCe', 'PETROL', TRUE),
(9, 'Megane', '1.5 dCi', 'DIESEL', TRUE);

-- Suzuki
INSERT INTO vehicle_models (brand_id, name, engine_type, fuel_type, active) VALUES
(10, 'Swift', '1.2 Dualjet', 'PETROL', TRUE),
(10, 'Vitara', '1.4 Boosterjet', 'PETROL', TRUE);

-- Szolgáltatástípusok táblája
CREATE TABLE IF NOT EXISTS service_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    description VARCHAR(255) NULL,
    duration_minutes INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- szolgáltatások feltöltése
INSERT INTO service_types (id, name, description, duration_minutes, active) VALUES
(1, 'Olajcsere', 'Motorolaj és olajszűrő cseréje, szintellenőrzés', 45, TRUE),
(2, 'Rutinvizsgálat', 'Általános állapotfelmérés, fékpad, folyadékszintek', 60, TRUE),
(3, 'Fékrendszer szerviz', 'Fékbetétek és féktárcsák ellenőrzése, cseréje', 90, TRUE),
(4, 'Műszaki vizsga felkészítés', 'Teljes körű átvizsgálás és felkészítés hatósági vizsgára', 120, TRUE),
(5, 'Klímatisztítás és töltés', 'Ózonos fertőtlenítés, hűtőközeg ellenőrzése és rátöltés', 45, TRUE),
(6, 'Futómű beállítás', 'Lézeres futómű geometria ellenőrzés és állítás', 60, TRUE)
ON DUPLICATE KEY UPDATE 
    description = VALUES(description),
    duration_minutes = VALUES(duration_minutes),
    active = VALUES(active);

-- Időpontfoglalások táblája
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    service_type_id BIGINT NOT NULL,
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'FINISHED') NOT NULL DEFAULT 'PENDING',
    customer_description VARCHAR(500) NULL,
    cancelled_at DATETIME NULL,
    cancellation_reason VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_customer FOREIGN KEY (customer_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_vehicle FOREIGN KEY (vehicle_id) 
        REFERENCES vehicles(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_service FOREIGN KEY (service_type_id) 
        REFERENCES service_types(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Munkalapok a szerelők számára
CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    assigned_mechanic_id BIGINT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'IN_PROGRESS', 'FINISHED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    accepted_at DATETIME NULL,
    started_at DATETIME NULL,
    closed_at DATETIME NULL,
    work_summary TEXT NULL,
    mechanic_note VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_tickets_booking FOREIGN KEY (booking_id) 
        REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_tickets_mechanic FOREIGN KEY (assigned_mechanic_id) 
        REFERENCES mechanic_profiles(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Munkalap státuszváltozások naplózása
CREATE TABLE IF NOT EXISTS ticket_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    changed_by_user_id BIGINT NOT NULL,
    old_status VARCHAR(50) NULL,
    new_status VARCHAR(50) NOT NULL,
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tsh_ticket FOREIGN KEY (ticket_id) 
        REFERENCES tickets(id) ON DELETE CASCADE,
    CONSTRAINT fk_tsh_user FOREIGN KEY (changed_by_user_id) 
        REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Indexek a gyorsabb kereséshez
CREATE INDEX idx_bookings_customer ON bookings(customer_id);
CREATE INDEX idx_bookings_dates ON bookings(start_at, end_at);
CREATE INDEX idx_tickets_status ON tickets(status);
CREATE INDEX idx_tickets_mechanic ON tickets(assigned_mechanic_id);