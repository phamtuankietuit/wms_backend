ALTER TABLE users
    ADD reset_token VARCHAR(255);

ALTER TABLE users
    ADD CONSTRAINT uc_users_resettoken UNIQUE (reset_token);