CREATE TABLE IF NOT EXISTS parameters
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    code        VARCHAR(20) UNIQUE NOT NULL,
    name        VARCHAR(100)       NOT NULL,
    unit        VARCHAR(20),
    description TEXT,
    category    VARCHAR(50),
    INDEX       idx_code(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
