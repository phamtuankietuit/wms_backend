ALTER TABLE roles
    ADD code VARCHAR(255);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_code UNIQUE (code);