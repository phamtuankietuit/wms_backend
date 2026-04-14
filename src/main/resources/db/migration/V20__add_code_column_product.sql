ALTER TABLE products
    ADD code VARCHAR(255);

ALTER TABLE products
    ALTER COLUMN code SET NOT NULL;

ALTER TABLE products
    ADD CONSTRAINT uc_products_code UNIQUE (code);