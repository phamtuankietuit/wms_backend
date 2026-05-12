ALTER TABLE users ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

UPDATE users SET status = CASE
    WHEN is_active = true THEN 'ACTIVE'
    ELSE 'DISABLED'
END;

ALTER TABLE users DROP COLUMN is_active;

CREATE INDEX idx_users_status ON users(status);
