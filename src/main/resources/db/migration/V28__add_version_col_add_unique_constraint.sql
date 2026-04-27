ALTER TABLE inventories
    ADD version BIGINT;

ALTER TABLE inventories
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE stock_transactions
    ADD version BIGINT;

ALTER TABLE stock_transactions
    ALTER COLUMN version SET NOT NULL;

ALTER TABLE inventories
    ADD CONSTRAINT uc_4c038f8c6fee215df0979a8d6 UNIQUE (variant_id, warehouse_id);