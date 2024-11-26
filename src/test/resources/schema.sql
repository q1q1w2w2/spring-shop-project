DROP TABLE IF EXISTS refresh_token;
CREATE TABLE refresh_token (
    idx int AUTO_INCREMENT PRIMARY KEY,
    user_idx int NOT NULL ,
    id VARCHAR(255) NOT NULL ,
    name VARCHAR(255) NOT NULL ,
    refreshtoken VARCHAR(500) NOT NULL ,
    expired_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
