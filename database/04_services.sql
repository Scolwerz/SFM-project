USE autoszerviz_db;

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