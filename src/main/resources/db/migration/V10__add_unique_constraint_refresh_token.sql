UPDATE users
SET refresh_token = NULL
WHERE refresh_token IS NOT NULL
  AND id NOT IN (
    SELECT MIN(id)
    FROM users
    WHERE refresh_token IS NOT NULL
    GROUP BY refresh_token
  );

ALTER TABLE users
    ADD CONSTRAINT uc_users_refresh_token UNIQUE (refresh_token);
