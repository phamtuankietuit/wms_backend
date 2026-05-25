ALTER TABLE attribute_values
    ADD CONSTRAINT uq_attribute_values UNIQUE (code, attribute_id);