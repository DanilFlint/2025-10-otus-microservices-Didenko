CREATE TABLE IF NOT EXISTS users
(
    id   BIGSERIAL PRIMARY KEY,
    name varchar(255),
    lastname varchar(255),
    login varchar(255) UNIQUE,
    password varchar(255)
);