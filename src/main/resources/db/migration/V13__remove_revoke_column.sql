ALTER TABLE refresh_tokens
    DROP COLUMN revoked_at;

CREATE INDEX idx_user_active ON refresh_tokens (user_id, deleted_at);

DROP INDEX idx_user_active;