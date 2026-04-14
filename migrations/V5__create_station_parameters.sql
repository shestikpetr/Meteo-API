CREATE TABLE IF NOT EXISTS station_parameters
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    station_id     INT         NOT NULL,
    parameter_code VARCHAR(20) NOT NULL,
    is_active      BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (station_id) REFERENCES stations (id) ON DELETE CASCADE,
    UNIQUE KEY unique_station_parameter (station_id, parameter_code),
    INDEX          idx_station_id(station_id),
    INDEX          idx_parameter_code(parameter_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
