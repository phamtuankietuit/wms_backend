ALTER TABLE users
    ADD CONSTRAINT uc_users_refreshtoken UNIQUE (refresh_token);