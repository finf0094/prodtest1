INSERT INTO roles (name)
VALUES ('ROLE_MODERATOR'), ('ROLE_USER')

-- Вставляем начальные данные в таблицу users
-- PASSWORD: 12345

INSERT INTO users (id, itin, firstname, password, phone, email, lastname)
VALUES (1, '0007275504', 'Асхат', '$2a$10$/R5FEJnBkO3cYOSghzv/Q.hsFAH/4iaV/4ZaUcLvauw6Tg.xEQCDW', '+77711551534', 'finf0094@gmail.com', 'Kulush');

-- -- Связываем пользователя с ролью
INSERT INTO users_roles (user_id, role_id)
VALUES (1, 1);
