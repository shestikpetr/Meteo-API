CREATE TABLE IF NOT EXISTS parameters
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    code        INT          NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    unit        VARCHAR(20),
    description TEXT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
