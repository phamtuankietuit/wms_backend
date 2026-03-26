CREATE TABLE refresh_tokens
(
    id           UUID                        NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at   TIMESTAMP WITHOUT TIME ZONE,
    token        VARCHAR(255)                NOT NULL,
    user_id      UUID                        NOT NULL,
    device_info  VARCHAR(500),
    ip_address   VARCHAR(255),
    device_name  VARCHAR(255),
    expires_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_used_at TIMESTAMP WITHOUT TIME ZONE,
    revoked_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id)
);

ALTER TABLE refresh_tokens
    ADD CONSTRAINT uc_refresh_tokens_token UNIQUE (token);

CREATE INDEX idx_expires_at ON refresh_tokens (expires_at);

CREATE INDEX idx_user_active ON refresh_tokens (user_id, revoked_at);

ALTER TABLE refresh_tokens
    ADD CONSTRAINT FK_REFRESH_TOKENS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_user_id ON refresh_tokens (user_id);

ALTER TABLE users
    DROP COLUMN refresh_token;