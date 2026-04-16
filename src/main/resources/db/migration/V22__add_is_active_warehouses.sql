ALTER TABLE warehouses
    ADD is_active BOOLEAN DEFAULT TRUE;

ALTER TABLE warehouses
    ALTER COLUMN is_active SET NOT NULL;