CREATE INDEX idx_stock_transaction_created_by ON stock_transactions(created_by);

CREATE INDEX idx_stock_transaction_updated_by ON stock_transactions(updated_by);

CREATE INDEX idx_stock_transaction_deleted_by ON stock_transactions(deleted_by);