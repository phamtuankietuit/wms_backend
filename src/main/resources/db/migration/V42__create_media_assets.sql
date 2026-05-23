CREATE TABLE media_assets
(
    id                UUID                        NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at        TIMESTAMP WITHOUT TIME ZONE,
    created_by        UUID,
    updated_by        UUID,
    deleted_by        UUID,
    owner_type        VARCHAR(50)                 NOT NULL,
    owner_id          UUID                        NOT NULL,
    public_id         VARCHAR(255)                NOT NULL,
    secure_url        VARCHAR(500)                NOT NULL,
    resource_type     VARCHAR(50)                 NOT NULL,
    format            VARCHAR(50),
    bytes             BIGINT,
    width             INT,
    height            INT,
    original_filename VARCHAR(255),
    sort_order        INT     DEFAULT 0           NOT NULL,
    is_primary        BOOLEAN DEFAULT FALSE       NOT NULL,
    CONSTRAINT pk_media_assets PRIMARY KEY (id),
    CONSTRAINT uq_media_assets_public_id UNIQUE (public_id),
    CONSTRAINT chk_media_assets_owner_type CHECK (owner_type IN ('USER', 'PRODUCT', 'VARIANT')),
    CONSTRAINT chk_media_assets_resource_type CHECK (resource_type IN ('image', 'raw', 'video'))
);

CREATE INDEX idx_media_assets_owner ON media_assets(owner_type, owner_id);

CREATE UNIQUE INDEX uq_primary_asset_per_entity
    ON media_assets(owner_type, owner_id)
    WHERE is_primary = TRUE;
