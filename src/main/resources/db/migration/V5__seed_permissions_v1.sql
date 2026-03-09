WITH seed(group_code, code, name) AS (
	VALUES
		('USER', 'USER_READ', 'Read User'),
		('USER', 'USER_CREATE', 'Create User'),
		('USER', 'USER_UPDATE', 'Update User'),
		('USER', 'USER_DELETE', 'Delete User'),

		('ROLE', 'ROLE_READ', 'Read Role'),
		('ROLE', 'ROLE_CREATE', 'Create Role'),
		('ROLE', 'ROLE_UPDATE', 'Update Role'),
		('ROLE', 'ROLE_DELETE', 'Delete Role'),

		('PERMISSION', 'PERMISSION_READ', 'Read Permission'),
		('PERMISSION', 'PERMISSION_CREATE', 'Create Permission'),
		('PERMISSION', 'PERMISSION_UPDATE', 'Update Permission'),
		('PERMISSION', 'PERMISSION_DELETE', 'Delete Permission'),

		('PERMISSION_GROUP', 'PERMISSION_GROUP_READ', 'Read Permission Group'),
		('PERMISSION_GROUP', 'PERMISSION_GROUP_CREATE', 'Create Permission Group'),
		('PERMISSION_GROUP', 'PERMISSION_GROUP_UPDATE', 'Update Permission Group'),
		('PERMISSION_GROUP', 'PERMISSION_GROUP_DELETE', 'Delete Permission Group'),

		('WAREHOUSE', 'WAREHOUSE_READ', 'Read Warehouse'),
		('WAREHOUSE', 'WAREHOUSE_CREATE', 'Create Warehouse'),
		('WAREHOUSE', 'WAREHOUSE_UPDATE', 'Update Warehouse'),
		('WAREHOUSE', 'WAREHOUSE_DELETE', 'Delete Warehouse'),

		('USER_WAREHOUSE', 'USER_WAREHOUSE_READ', 'Read User Warehouse'),
		('USER_WAREHOUSE', 'USER_WAREHOUSE_ASSIGN', 'Assign User Warehouse'),
		('USER_WAREHOUSE', 'USER_WAREHOUSE_UNASSIGN', 'Unassign User Warehouse')
), prepared AS (
	SELECT
		gen_random_uuid() AS id,
		(
			SELECT pg.id
			FROM permission_groups pg
			WHERE pg.code = s.group_code
		) AS group_id,
		s.code,
		s.name
	FROM seed s
)

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
	deleted_at = NULL;
