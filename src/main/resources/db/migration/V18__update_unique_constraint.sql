ALTER TABLE attributes
    ADD CONSTRAINT uc_attributes_code UNIQUE (code);

ALTER TABLE attribute_values
    ADD CONSTRAINT uq_attribute_values UNIQUE (code, attribute_id);