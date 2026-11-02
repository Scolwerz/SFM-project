USE autoszerviz_db;

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