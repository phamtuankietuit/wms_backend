ALTER TABLE attribute_values
    ADD is_active BOOLEAN DEFAULT TRUE;

ALTER TABLE attribute_values
    ALTER COLUMN is_active SET NOT NULL;

ALTER TABLE attributes
    ADD is_active BOOLEAN DEFAULT TRUE;

ALTER TABLE attributes
    ALTER COLUMN is_active SET NOT NULL;