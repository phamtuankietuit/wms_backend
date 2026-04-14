ALTER TABLE attribute_values
    ADD created_by UUID;

ALTER TABLE attribute_values
    ADD deleted_by UUID;

ALTER TABLE attribute_values
    ADD updated_by UUID;

ALTER TABLE attributes
    ADD created_by UUID;

ALTER TABLE attributes
    ADD deleted_by UUID;

ALTER TABLE attributes
    ADD updated_by UUID;

ALTER TABLE permission_groups
    ADD created_by UUID;

ALTER TABLE permission_groups
    ADD deleted_by UUID;

ALTER TABLE permission_groups
    ADD updated_by UUID;

ALTER TABLE permissions
    ADD created_by UUID;

ALTER TABLE permissions
    ADD deleted_by UUID;

ALTER TABLE permissions
    ADD updated_by UUID;

ALTER TABLE products
    ADD created_by UUID;

ALTER TABLE products
    ADD deleted_by UUID;

ALTER TABLE products
    ADD updated_by UUID;

ALTER TABLE refresh_tokens
    ADD created_by UUID;

ALTER TABLE refresh_tokens
    ADD deleted_by UUID;

ALTER TABLE refresh_tokens
    ADD updated_by UUID;

ALTER TABLE roles
    ADD created_by UUID;

ALTER TABLE roles
    ADD deleted_by UUID;

ALTER TABLE roles
    ADD updated_by UUID;

ALTER TABLE users
    ADD created_by UUID;

ALTER TABLE users
    ADD deleted_by UUID;

ALTER TABLE users
    ADD updated_by UUID;

ALTER TABLE variants
    ADD created_by UUID;

ALTER TABLE variants
    ADD deleted_by UUID;

ALTER TABLE variants
    ADD updated_by UUID;

ALTER TABLE warehouses
    ADD created_by UUID;

ALTER TABLE warehouses
    ADD deleted_by UUID;

ALTER TABLE warehouses
    ADD updated_by UUID;