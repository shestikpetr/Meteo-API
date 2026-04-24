CREATE TABLE IF NOT EXISTS station_parameters
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    station_id   INT     NOT NULL,
    parameter_id INT     NOT NULL,
    is_active    BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_station_parameters_station
        FOREIGN KEY (station_id) REFERENCES stations (id) ON DELETE CASCADE,
    CONSTRAINT fk_station_parameters_parameter
        FOREIGN KEY (parameter_id) REFERENCES parameters (id) ON DELETE CASCADE,
    CONSTRAINT unique_station_parameter UNIQUE (station_id, parameter_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
