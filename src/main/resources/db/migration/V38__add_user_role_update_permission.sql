WITH seed(group_code, code, name) AS (
	VALUES
		('USER', 'USER_ROLE_UPDATE', 'Update User Roles')
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
), seed_role_permissions(role_code, permission_code) AS (
	VALUES
		('SUPER_ADMIN', 'USER_ROLE_UPDATE'),
		('SYSTEM_ADMIN', 'USER_ROLE_UPDATE')
), resolved AS (
	SELECT
		p.id AS permission_id,
		r.id AS role_id
	FROM seed_role_permissions s
	JOIN roles r ON r.code = s.role_code
	JOIN permissions p ON p.code = s.permission_code
)
INSERT INTO roles_permissions (permission_id, role_id)
SELECT permission_id, role_id
FROM resolved
ON CONFLICT (permission_id, role_id) DO NOTHING;
