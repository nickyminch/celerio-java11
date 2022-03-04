-- create addresses for accounts
INSERT INTO Address (street_name, city) values('Park avenue', 'New-York');
INSERT INTO Address (street_name, city) values('Tochomae', 'Tokyo');
INSERT INTO Address (street_name, city) values('California Street', 'San Francisco');

-- create dummy accounts
INSERT INTO User (id, login, password, email, is_enabled, address_id) VALUES ('000000000000000000000000000000000001', 'admin', 'admin', 'admin@example.com', true, 1);
INSERT INTO User (id, login, password, email, is_enabled, address_id) VALUES ('000000000000000000000000000000000002', 'user',  'user',  'user@example.com', true, 2);
INSERT INTO User (id, login, password, email, is_enabled, address_id) VALUES ('000000000000000000000000000000000003', 'demo',  'demo',  'demo@example.com', true, 3);

-- define available roles
INSERT INTO Role (role_name) VALUES ('ROLE_ADMIN');
INSERT INTO Role (role_name) VALUES ('ROLE_USER');
INSERT INTO Role (role_name) VALUES ('ROLE_MONITORING');

-- admin has admin, user and monitoring roles
INSERT INTO User_Role (user_id, role_id) VALUES ('000000000000000000000000000000000001', 1);
INSERT INTO User_Role (user_id, role_id) VALUES ('000000000000000000000000000000000001', 2);
INSERT INTO User_Role (user_id, role_id) VALUES ('000000000000000000000000000000000001', 3);
-- user has user role
INSERT INTO User_Role (user_id, role_id) VALUES ('000000000000000000000000000000000002', 2);
-- demo has user role
INSERT INTO User_Role (user_id, role_id) VALUES ('000000000000000000000000000000000003', 2);

-- products for admin
INSERT INTO Product(name, quantity) VALUES ('Prime Faces User Guide' , 444);
INSERT INTO Product(name, quantity) VALUES ('Spring Web Flow Reference Guide', 108);
