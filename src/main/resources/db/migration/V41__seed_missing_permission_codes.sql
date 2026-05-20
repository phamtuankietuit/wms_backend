WITH seed(code, name) AS (
	VALUES
		('PRODUCT', 'Product Management'),
		('ATTRIBUTE', 'Attribute Management'),
		('ATTRIBUTE_VALUE', 'Attribute Value Management'),
		('STOCK_TRANSACTION', 'Stock Transaction Management'),
		('INVENTORY', 'Inventory Management')
)
INSERT INTO permission_groups (id, created_at, updated_at, deleted_at, code, name)
SELECT
	gen_random_uuid(),
	NOW(),
	NOW(),
	NULL,
	code,
	name
FROM seed
ON CONFLICT (code)
DO UPDATE SET
	name = EXCLUDED.name,
	updated_at = NOW(),
	deleted_at = NULL;

WITH seed(group_code, code, name) AS (
	VALUES
		('USER', 'USER_RESTORE', 'Restore User'),

		('WAREHOUSE', 'WAREHOUSE_RESTORE', 'Restore Warehouse'),

		('PRODUCT', 'PRODUCT_CREATE', 'Create Product'),
		('PRODUCT', 'PRODUCT_READ', 'Read Product'),
		('PRODUCT', 'PRODUCT_UPDATE', 'Update Product'),
		('PRODUCT', 'PRODUCT_DELETE', 'Delete Product'),
		('PRODUCT', 'PRODUCT_RESTORE', 'Restore Product'),

		('ATTRIBUTE', 'ATTRIBUTE_CREATE', 'Create Attribute'),
		('ATTRIBUTE', 'ATTRIBUTE_READ', 'Read Attribute'),
		('ATTRIBUTE', 'ATTRIBUTE_UPDATE', 'Update Attribute'),
		('ATTRIBUTE', 'ATTRIBUTE_DELETE', 'Delete Attribute'),

		('ATTRIBUTE_VALUE', 'ATTRIBUTE_VALUE_CREATE', 'Create Attribute Value'),
		('ATTRIBUTE_VALUE', 'ATTRIBUTE_VALUE_READ', 'Read Attribute Value'),
		('ATTRIBUTE_VALUE', 'ATTRIBUTE_VALUE_UPDATE', 'Update Attribute Value'),
		('ATTRIBUTE_VALUE', 'ATTRIBUTE_VALUE_DELETE', 'Delete Attribute Value'),

		('STOCK_TRANSACTION', 'STOCK_TRANSACTION_CREATE', 'Create Stock Transaction'),
		('STOCK_TRANSACTION', 'STOCK_TRANSACTION_READ', 'Read Stock Transaction'),
		('STOCK_TRANSACTION', 'STOCK_TRANSACTION_UPDATE', 'Update Stock Transaction'),
		('STOCK_TRANSACTION', 'STOCK_TRANSACTION_DELETE', 'Delete Stock Transaction'),

		('INVENTORY', 'INVENTORY_READ', 'Read Inventory')
), prepared AS (
	SELECT
		gen_random_uuid() AS id,
		pg.id AS group_id,
		s.code,
		s.name
	FROM seed s
	JOIN permission_groups pg ON pg.code = s.group_code
), inserted_permissions AS (
	INSERT INTO permissions (id, created_at, updated_at, deleted_at, group_id, code, name)
	SELECT
		id,
		NOW(),
		NOW(),
		NULL,
		group_id,
		code,
		name
	FROM prepared
	ON CONFLICT (code)
	DO UPDATE SET
		group_id = EXCLUDED.group_id,
		name = EXCLUDED.name,
		updated_at = NOW(),
		deleted_at = NULL
	RETURNING id, code
), resolved AS (
	SELECT
		p.id AS permission_id,
		r.id AS role_id
	FROM inserted_permissions p
	JOIN roles r ON r.code = 'SUPER_ADMIN'
)
INSERT INTO roles_permissions (permission_id, role_id)
SELECT permission_id, role_id
FROM resolved
ON CONFLICT (permission_id, role_id) DO NOTHING;
