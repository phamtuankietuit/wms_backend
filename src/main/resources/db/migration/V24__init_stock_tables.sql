CREATE TABLE inventories
(
    id                UUID                        NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at        TIMESTAMP WITHOUT TIME ZONE,
    created_by        UUID,
    updated_by        UUID,
    deleted_by        UUID,
    variant_id        UUID                        NOT NULL,
    warehouse_id      UUID                        NOT NULL,
    quantity          BIGINT DEFAULT 0            NOT NULL,
    reserved_quantity BIGINT DEFAULT 0            NOT NULL,
    CONSTRAINT pk_inventories PRIMARY KEY (id)
);

CREATE TABLE stock_transaction_items
(
    id                   UUID                        NOT NULL,
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at           TIMESTAMP WITHOUT TIME ZONE,
    created_by           UUID,
    updated_by           UUID,
    deleted_by           UUID,
    variant_id           UUID                        NOT NULL,
    stock_transaction_id UUID                        NOT NULL,
    quantity             BIGINT                      NOT NULL,
    CONSTRAINT pk_stock_transaction_items PRIMARY KEY (id)
);

CREATE TABLE stock_transactions
(
    id           UUID                        NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at   TIMESTAMP WITHOUT TIME ZONE,
    created_by   UUID,
    updated_by   UUID,
    deleted_by   UUID,
    warehouse_id UUID                        NOT NULL,
    type         VARCHAR(255)                NOT NULL,
    status       VARCHAR(20)                 NOT NULL,
    note         TEXT,
    CONSTRAINT pk_stock_transactions PRIMARY KEY (id)
);

ALTER TABLE inventories
    ADD CONSTRAINT FK_INVENTORIES_ON_VARIANT FOREIGN KEY (variant_id) REFERENCES variants (id);

ALTER TABLE inventories
    ADD CONSTRAINT FK_INVENTORIES_ON_WAREHOUSE FOREIGN KEY (warehouse_id) REFERENCES warehouses (id);

ALTER TABLE stock_transactions
    ADD CONSTRAINT FK_STOCK_TRANSACTIONS_ON_WAREHOUSE FOREIGN KEY (warehouse_id) REFERENCES warehouses (id);

ALTER TABLE stock_transaction_items
    ADD CONSTRAINT FK_STOCK_TRANSACTION_ITEMS_ON_STOCK_TRANSACTION FOREIGN KEY (stock_transaction_id) REFERENCES stock_transactions (id);

ALTER TABLE stock_transaction_items
    ADD CONSTRAINT FK_STOCK_TRANSACTION_ITEMS_ON_VARIANT FOREIGN KEY (variant_id) REFERENCES variants (id);