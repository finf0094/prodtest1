


-- Создаем таблицу roles
CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL UNIQUE
);

-- Создаем таблицу Resume
CREATE TABLE Resume (
                        id VARCHAR(255) PRIMARY KEY,
                        iin VARCHAR(255),
                        file_name VARCHAR(255),
                        file_path VARCHAR(255)
);

-- Создаем таблицу users
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       itin VARCHAR(12) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       firstname VARCHAR(255),
                       lastname VARCHAR(255),
                       password VARCHAR(255) NOT NULL,
                       position VARCHAR(255),
                       phone VARCHAR(255) UNIQUE
);

-- Создаем таблицу users_roles
CREATE TABLE users_roles (
                             user_id BIGINT NOT NULL,
                             role_id INT NOT NULL,
                             PRIMARY KEY (user_id, role_id),
                             FOREIGN KEY (user_id) REFERENCES users (id),
                             FOREIGN KEY (role_id) REFERENCES roles (id)
);


INSERT INTO roles (name)
VALUES ('ROLE_MODERATOR'), ('ROLE_USER');


-- Вставляем начальные данные в таблицу users
-- PASSWORD: tete9291

INSERT INTO users (id, itin, firstname, password, phone, email, lastname, position)
VALUES (1000,
        '871214501234',
        'Айдын',
        '$2a$10$K7YNSx1toWbxSSg6P3MigeYs.4ZikPjwcDPgMvbk7j1sCoh2dYSGy',
        '+77011234567',
        'aidin@example.com',
        'Қожаш Сырымұлы',
        'Начальник отдела'
        );

-- Связываем пользователя с ролью
INSERT INTO users_roles (user_id, role_id)
VALUES (1000, 1), (1000, 2);


