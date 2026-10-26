USE autoszerviz_db;

-- 1. Autómárkák
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

-- 2. Modellek motorral és üzemanyaggal (fuel_type)
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