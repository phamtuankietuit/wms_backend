ALTER TABLE stock_transaction_histories
    ADD assigned_to UUID;

ALTER TABLE stock_transaction_histories
    ALTER COLUMN assigned_to SET NOT NULL;

ALTER TABLE stock_transactions
    ADD assigned_to UUID;

ALTER TABLE stock_transactions
    ALTER COLUMN assigned_to SET NOT NULL;

ALTER TABLE stock_transactions
    ADD CONSTRAINT FK_STOCK_TRANSACTIONS_ON_ASSIGNED_TO FOREIGN KEY (assigned_to) REFERENCES users (id);

ALTER TABLE stock_transaction_histories
    ADD CONSTRAINT FK_STOCK_TRANSACTION_HISTORIES_ON_ASSIGNED_TO FOREIGN KEY (assigned_to) REFERENCES users (id);