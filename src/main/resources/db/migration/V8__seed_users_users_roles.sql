CREATE EXTENSION IF NOT EXISTS "pgcrypto";

WITH seed_users(email, full_name, role_code, password) AS (
	VALUES
		('kitne1@yopmail.com', 'Kitne User 1', 'SYSTEM_ADMIN', '$2a$12$MvODtILkJwTTZJ/t4gHKf.6iwJLRCbtRRoq6R9C4wMPQ5FknT6P7u'),
		('kitne2@yopmail.com', 'Kitne User 2', 'SUPER_ADMIN', '$2a$12$MvODtILkJwTTZJ/t4gHKf.6iwJLRCbtRRoq6R9C4wMPQ5FknT6P7u'),
		('kitne3@yopmail.com', 'Kitne User 3', 'ADMIN', '$2a$12$MvODtILkJwTTZJ/t4gHKf.6iwJLRCbtRRoq6R9C4wMPQ5FknT6P7u')
), upsert_users AS (
	INSERT INTO users (id, created_at, updated_at, deleted_at, email, password, access_token, name, date_of_birth, avatar)
	SELECT
		gen_random_uuid(),
		NOW(),
		NOW(),
		NULL,
		s.email,
		s.password,
		NULL,
		s.full_name,
		NULL,
		NULL
	FROM seed_users s
	ON CONFLICT (email)
	DO UPDATE SET
		password = EXCLUDED.password,
		name = EXCLUDED.name,
		updated_at = NOW(),
		deleted_at = NULL
	RETURNING id, email
), resolved AS (
	SELECT
		COALESCE(u.id, created.id) AS user_id,
		r.id AS role_id
	FROM seed_users s
	LEFT JOIN upsert_users created ON created.email = s.email
	LEFT JOIN users u ON u.email = s.email
	JOIN roles r ON r.code = s.role_code
)
INSERT INTO users_roles (role_id, user_id)
SELECT role_id, user_id
FROM resolved
ON CONFLICT (role_id, user_id) DO NOTHING;
