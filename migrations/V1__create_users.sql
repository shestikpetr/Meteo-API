CREATE TABLE IF NOT EXISTS users
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)            NOT NULL UNIQUE,
    email         VARCHAR(255)           NOT NULL UNIQUE,
    password_hash VARCHAR(255)           NOT NULL,
    is_active     BOOLEAN                NOT NULL DEFAULT TRUE,
    role          ENUM ('user', 'admin') NOT NULL DEFAULT 'user',
    created_at    TIMESTAMP              NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
