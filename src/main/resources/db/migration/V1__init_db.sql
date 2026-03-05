CREATE TABLE permission_groups
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    code       VARCHAR(100)                NOT NULL,
    name       VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_permission_groups PRIMARY KEY (id)
);

CREATE TABLE permissions
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    group_id   UUID                        NOT NULL,
    code       VARCHAR(100)                NOT NULL,
    name       VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_permissions PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id             UUID                        NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at     TIMESTAMP WITHOUT TIME ZONE,
    name           VARCHAR(100)                NOT NULL,
    is_admin_role  BOOLEAN                     NOT NULL,
    is_system_role BOOLEAN                     NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE roles_permissions
(
    permission_id UUID NOT NULL,
    role_id       UUID NOT NULL,
    CONSTRAINT pk_roles_permissions PRIMARY KEY (permission_id, role_id)
);

CREATE TABLE users
(
    id            UUID                        NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at    TIMESTAMP WITHOUT TIME ZONE,
    email         VARCHAR(255)                NOT NULL,
    password      VARCHAR(255)                NOT NULL,
    access_token  VARCHAR(255),
    name          VARCHAR(255)                NOT NULL,
    date_of_birth date,
    avatar        VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE users_roles
(
    role_id UUID NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT pk_users_roles PRIMARY KEY (role_id, user_id)
);

CREATE TABLE users_warehouses
(
    assigned_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id      UUID                        NOT NULL,
    warehouse_id UUID                        NOT NULL,
    CONSTRAINT pk_users_warehouses PRIMARY KEY (user_id, warehouse_id)
);

CREATE TABLE warehouses
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    code       VARCHAR(255)                NOT NULL,
    name       VARCHAR(255),
    CONSTRAINT pk_warehouses PRIMARY KEY (id)
);

ALTER TABLE permission_groups
    ADD CONSTRAINT uc_permission_groups_code UNIQUE (code);

ALTER TABLE permissions
    ADD CONSTRAINT uc_permissions_code UNIQUE (code);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_name UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE warehouses
    ADD CONSTRAINT uc_warehouses_code UNIQUE (code);

ALTER TABLE permissions
    ADD CONSTRAINT FK_PERMISSIONS_ON_GROUP FOREIGN KEY (group_id) REFERENCES permission_groups (id);

ALTER TABLE users_warehouses
    ADD CONSTRAINT FK_USERS_WAREHOUSES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE users_warehouses
    ADD CONSTRAINT FK_USERS_WAREHOUSES_ON_WAREHOUSE FOREIGN KEY (warehouse_id) REFERENCES warehouses (id);

ALTER TABLE roles_permissions
    ADD CONSTRAINT fk_rolper_on_permission FOREIGN KEY (permission_id) REFERENCES permissions (id);

ALTER TABLE roles_permissions
    ADD CONSTRAINT fk_rolper_on_role FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (id);