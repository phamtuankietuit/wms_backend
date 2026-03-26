ALTER TABLE refresh_tokens
    ADD jti VARCHAR(255);

ALTER TABLE refresh_tokens
    ADD user_agent VARCHAR(500);

ALTER TABLE refresh_tokens
    ALTER COLUMN jti SET NOT NULL;

ALTER TABLE refresh_tokens
    ADD CONSTRAINT uc_refresh_tokens_jti UNIQUE (jti);

CREATE INDEX idx_user_active ON refresh_tokens (user_id, deleted_at);

ALTER TABLE refresh_tokens
    DROP COLUMN device_info;

ALTER TABLE refresh_tokens
    DROP COLUMN device_name;