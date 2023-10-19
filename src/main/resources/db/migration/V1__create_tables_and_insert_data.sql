-- Код миграции для создания таблиц и вставки начальных данных

-- Создаем таблицу roles
CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL UNIQUE
);

-- Создаем таблицу Resume
CREATE TABLE Resume (
                        id VARCHAR(255) PRIMARY KEY,
                        iin VARCHAR(255),
                        fileName VARCHAR(255),
                        filePath VARCHAR(255)
);

-- Создаем таблицу users
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       itin VARCHAR(12) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       firstname VARCHAR(255),
                       lastname VARCHAR(255),
                       password VARCHAR(255) NOT NULL,
                       phone VARCHAR(255) UNIQUE
);

-- Создаем таблицу users_roles
CREATE TABLE users_roles (
                             user_id BIGINT NOT NULL,
                             role_id INT NOT NULL,
                             PRIMARY KEY (user_id, role_id),
                             FOREIGN KEY (user_id) REFERENCES users (id),
                             FOREIGN KEY (role_id) REFERENCES roles (id)
)


-- INSERT INTO roles (name)
-- VALUES ('ROLE_MODERATOR'), ('ROLE_USER')


-- Вставляем начальные данные в таблицу users
-- PASSWORD: 12345

-- INSERT INTO users (id, itin, firstname, password, phone, email, lastname)
-- VALUES (1, '0007275504', 'Асхат', '$2a$10$/R5FEJnBkO3cYOSghzv/Q.hsFAH/4iaV/4ZaUcLvauw6Tg.xEQCDW', '+77711551534', 'finf0094@gmail.com', 'Kulush');
--
-- -- Связываем пользователя с ролью
-- INSERT INTO users_roles (user_id, role_id)
-- VALUES (1, 1);
