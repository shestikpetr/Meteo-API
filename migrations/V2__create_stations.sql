CREATE TABLE IF NOT EXISTS stations
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    station_number VARCHAR(20)  NOT NULL UNIQUE,
    name           VARCHAR(100) NOT NULL,
    location       VARCHAR(200),
    latitude       DECIMAL(10, 6),
    longitude      DECIMAL(10, 6),
    altitude       DECIMAL(10, 2),
    is_active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
