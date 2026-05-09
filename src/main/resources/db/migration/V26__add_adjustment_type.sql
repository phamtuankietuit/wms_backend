ALTER TABLE stock_transaction_items
    ADD adjustment_type VARCHAR(50);

ALTER TABLE stock_transaction_items
    ALTER COLUMN adjustment_type SET NOT NULL;