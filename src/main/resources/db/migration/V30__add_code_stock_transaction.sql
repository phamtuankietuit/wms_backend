ALTER TABLE stock_transactions
    ADD code VARCHAR(100);

ALTER TABLE stock_transactions
    ADD CONSTRAINT uc_stock_transactions_code UNIQUE (code);