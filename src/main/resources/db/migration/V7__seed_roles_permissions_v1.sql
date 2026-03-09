WITH manual_seed(role_code, permission_code) AS (
	VALUES
		('SYSTEM_ADMIN', 'USER_READ'),
		('SYSTEM_ADMIN', 'USER_CREATE'),
		('SYSTEM_ADMIN', 'USER_UPDATE'),
		('SYSTEM_ADMIN', 'USER_DELETE'),

		('SYSTEM_ADMIN', 'ROLE_READ'),
		('SYSTEM_ADMIN', 'ROLE_CREATE'),
		('SYSTEM_ADMIN', 'ROLE_UPDATE'),
		('SYSTEM_ADMIN', 'ROLE_DELETE'),

		('SYSTEM_ADMIN', 'PERMISSION_READ'),
		('SYSTEM_ADMIN', 'PERMISSION_CREATE'),
		('SYSTEM_ADMIN', 'PERMISSION_UPDATE'),
		('SYSTEM_ADMIN', 'PERMISSION_DELETE'),

		('SYSTEM_ADMIN', 'PERMISSION_GROUP_READ'),
		('SYSTEM_ADMIN', 'PERMISSION_GROUP_CREATE'),
		('SYSTEM_ADMIN', 'PERMISSION_GROUP_UPDATE'),
		('SYSTEM_ADMIN', 'PERMISSION_GROUP_DELETE'),

		('ADMIN', 'WAREHOUSE_READ'),
		('ADMIN', 'WAREHOUSE_UPDATE')
), super_admin_seed(role_code, permission_code) AS (
	SELECT
		'SUPER_ADMIN' AS role_code,
		p.code AS permission_code
	FROM permissions p
), seed(role_code, permission_code) AS (
	SELECT role_code, permission_code FROM manual_seed
	UNION ALL
	SELECT role_code, permission_code FROM super_admin_seed
), resolved AS (
	SELECT
		p.id AS permission_id,
		r.id AS role_id
	FROM seed s
	JOIN roles r ON r.code = s.role_code
	JOIN permissions p ON p.code = s.permission_code
)
INSERT INTO roles_permissions (permission_id, role_id)
SELECT permission_id, role_id
FROM resolved
ON CONFLICT (permission_id, role_id) DO NOTHING;
