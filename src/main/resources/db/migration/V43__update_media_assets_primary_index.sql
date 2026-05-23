DROP INDEX IF EXISTS uq_primary_asset_per_entity;

CREATE UNIQUE INDEX uq_primary_asset_per_entity
    ON media_assets(owner_type, owner_id)
    WHERE is_primary = TRUE AND deleted_at IS NULL;
