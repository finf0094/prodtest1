-- Код миграции для создания таблиц и вставки начальных данных

-- Создаем таблицу roles
CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL UNIQUE
);

-- Создаем таблицу Resume
CREATE TABLE Resume (
                        id VARCHAR(255) PRIMARY KEY,
                        iin BIGINT,
                        file_name VARCHAR(255),
                        file_path VARCHAR(255)
);

-- Создаем таблицу users
CREATE TABLE users (
                       itin BIGINT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       firstname VARCHAR(255),
                       lastname VARCHAR(255),
                       password VARCHAR(255) NOT NULL,
                       phone VARCHAR(255) UNIQUE,
                       position VARCHAR(255)
);

-- Создаем таблицу users_roles
CREATE TABLE users_roles (
                             user_id BIGINT NOT NULL,
                             role_id INT NOT NULL,
                             PRIMARY KEY (user_id, role_id),
                             FOREIGN KEY (user_id) REFERENCES users (itin),
                             FOREIGN KEY (role_id) REFERENCES roles (id)
);

-- Вставляем начальные данные в таблицу roles
INSERT INTO roles (name)
VALUES ('ROLE_MODERATOR'), ('ROLE_USER');

-- Вставляем начальные данные в таблицу users
-- PASSWORD: 12345
INSERT INTO users (itin, firstname, password, phone, email, lastname, position)
VALUES ('0412165506', 'Асхат', '$2a$10$/R5FEJnBkO3cYOSghzv/Q.hsFAH/4iaV/4ZaUcLvauw6Tg.xEQCDW', '+77711551534', 'finf0094@gmail.com', 'Kulush', 'IT');

-- Связываем пользователя с ролью
INSERT INTO users_roles (user_id, role_id)
VALUES (0412165506, 1);
