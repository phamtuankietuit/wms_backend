CREATE TABLE attribute_values
(
    id           UUID                        NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at   TIMESTAMP WITHOUT TIME ZONE,
    attribute_id UUID                        NOT NULL,
    code         VARCHAR(100)                NOT NULL,
    value        VARCHAR(100)                NOT NULL,
    CONSTRAINT pk_attribute_values PRIMARY KEY (id)
);

CREATE TABLE attributes
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    code       VARCHAR(100)                NOT NULL,
    name       VARCHAR(100)                NOT NULL,
    CONSTRAINT pk_attributes PRIMARY KEY (id)
);

CREATE TABLE products
(
    id          UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at  TIMESTAMP WITHOUT TIME ZONE,
    name        VARCHAR(255)                NOT NULL,
    description TEXT,
    is_active   BOOLEAN,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

CREATE TABLE products_attributes
(
    is_active    BOOLEAN,
    product_id   UUID NOT NULL,
    attribute_id UUID NOT NULL,
    CONSTRAINT pk_products_attributes PRIMARY KEY (product_id, attribute_id)
);

CREATE TABLE variants
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    product_id UUID                        NOT NULL,
    sku        VARCHAR(255)                NOT NULL,
    is_active  BOOLEAN,
    CONSTRAINT pk_variants PRIMARY KEY (id)
);

CREATE TABLE variants_attribute_values
(
    is_active          BOOLEAN,
    variant_id         UUID NOT NULL,
    attribute_value_id UUID NOT NULL,
    CONSTRAINT pk_variants_attribute_values PRIMARY KEY (variant_id, attribute_value_id)
);

ALTER TABLE attributes
    ADD CONSTRAINT uc_attributes_code UNIQUE (code);

ALTER TABLE variants
    ADD CONSTRAINT uc_variants_sku UNIQUE (sku);

ALTER TABLE attribute_values
    ADD CONSTRAINT uk_attribute_code UNIQUE (attribute_id, code);

ALTER TABLE attribute_values
    ADD CONSTRAINT FK_ATTRIBUTE_VALUES_ON_ATTRIBUTE FOREIGN KEY (attribute_id) REFERENCES attributes (id);

ALTER TABLE products_attributes
    ADD CONSTRAINT FK_PRODUCTS_ATTRIBUTES_ON_ATTRIBUTE FOREIGN KEY (attribute_id) REFERENCES attributes (id);

ALTER TABLE products_attributes
    ADD CONSTRAINT FK_PRODUCTS_ATTRIBUTES_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE variants_attribute_values
    ADD CONSTRAINT FK_VARIANTS_ATTRIBUTE_VALUES_ON_ATTRIBUTE_VALUE FOREIGN KEY (attribute_value_id) REFERENCES attribute_values (id);

ALTER TABLE variants_attribute_values
    ADD CONSTRAINT FK_VARIANTS_ATTRIBUTE_VALUES_ON_VARIANT FOREIGN KEY (variant_id) REFERENCES variants (id);

ALTER TABLE variants
    ADD CONSTRAINT FK_VARIANTS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);