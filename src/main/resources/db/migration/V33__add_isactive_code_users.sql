ALTER TABLE users
    ADD is_active BOOLEAN DEFAULT TRUE;

ALTER TABLE users
    ADD code VARCHAR(100);

ALTER TABLE users
    ADD CONSTRAINT uc_users_code UNIQUE (code);