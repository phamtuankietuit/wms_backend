ALTER TABLE users
    ADD refresh_token VARCHAR(255);

ALTER TABLE users
    DROP COLUMN access_token;