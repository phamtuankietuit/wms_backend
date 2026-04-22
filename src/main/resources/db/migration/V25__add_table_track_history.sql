CREATE TABLE inventory_movements
(
    id                   UUID                        NOT NULL,
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at           TIMESTAMP WITHOUT TIME ZONE,
    created_by           UUID,
    updated_by           UUID,
    deleted_by           UUID,
    inventory_id         UUID                        NOT NULL,
    stock_transaction_id UUID                        NOT NULL,
    quantity_change      BIGINT DEFAULT 0            NOT NULL,
    before_quantity      BIGINT DEFAULT 0            NOT NULL,
    after_quantity       BIGINT DEFAULT 0            NOT NULL,
    CONSTRAINT pk_inventory_movements PRIMARY KEY (id)
);

CREATE TABLE stock_transaction_histories
(
    id                   UUID                        NOT NULL,
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at           TIMESTAMP WITHOUT TIME ZONE,
    created_by           UUID,
    updated_by           UUID,
    deleted_by           UUID,
    stock_transaction_id UUID                        NOT NULL,
    from_status          VARCHAR(20)                 NOT NULL,
    to_status            VARCHAR(20)                 NOT NULL,
    note                 TEXT,
    reason               TEXT,
    CONSTRAINT pk_stock_transaction_histories PRIMARY KEY (id)
);

ALTER TABLE inventory_movements
    ADD CONSTRAINT FK_INVENTORY_MOVEMENTS_ON_INVENTORY FOREIGN KEY (inventory_id) REFERENCES inventories (id);

ALTER TABLE inventory_movements
    ADD CONSTRAINT FK_INVENTORY_MOVEMENTS_ON_STOCK_TRANSACTION FOREIGN KEY (stock_transaction_id) REFERENCES stock_transactions (id);

ALTER TABLE stock_transaction_histories
    ADD CONSTRAINT FK_STOCK_TRANSACTION_HISTORIES_ON_STOCK_TRANSACTION FOREIGN KEY (stock_transaction_id) REFERENCES stock_transactions (id);