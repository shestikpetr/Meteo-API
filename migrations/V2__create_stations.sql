CREATE TABLE IF NOT EXISTS stations
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    station_number VARCHAR(20) UNIQUE NOT NULL,
    name           VARCHAR(100)       NOT NULL,
    location       VARCHAR(200),
    latitude       DECIMAL(10, 6),
    longitude      DECIMAL(10, 6),
    altitude       DECIMAL(10, 2),
    is_active      BOOLEAN   DEFAULT TRUE,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX          idx_station_number(station_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
