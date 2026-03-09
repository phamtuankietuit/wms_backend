ALTER TABLE users
    ADD refresh_token TEXT;

ALTER TABLE users
    DROP COLUMN access_token;