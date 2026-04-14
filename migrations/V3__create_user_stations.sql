CREATE TABLE IF NOT EXISTS user_stations
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    station_id  INT NOT NULL,
    custom_name VARCHAR(100),
    is_favorite BOOLEAN   DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (station_id) REFERENCES stations (id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_station (user_id, station_id),
    INDEX       idx_user_id(user_id),
    INDEX       idx_station_id(station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
