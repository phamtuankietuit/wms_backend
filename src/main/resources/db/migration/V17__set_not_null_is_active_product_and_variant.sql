ALTER TABLE products
    ALTER COLUMN is_active SET NOT NULL;

ALTER TABLE products
    ALTER COLUMN is_active SET DEFAULT TRUE;

ALTER TABLE products_attributes
    ALTER COLUMN is_active SET NOT NULL;

ALTER TABLE products_attributes
    ALTER COLUMN is_active SET DEFAULT TRUE;

ALTER TABLE variants_attribute_values
    ALTER COLUMN is_active SET NOT NULL;

ALTER TABLE variants_attribute_values
    ALTER COLUMN is_active SET DEFAULT TRUE;