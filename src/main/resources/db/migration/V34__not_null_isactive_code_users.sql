UPDATE users
SET is_active = TRUE
WHERE is_active IS NULL;

WITH numbered_users AS (
    SELECT
        id,
        ROW_NUMBER() OVER (ORDER BY created_at, id) AS rn
    FROM users
    WHERE code IS NULL
)
UPDATE users u
SET code = 'USR-SEED-' || LPAD(numbered_users.rn::TEXT, 6, '0')
FROM numbered_users
WHERE u.id = numbered_users.id;

ALTER TABLE users
    ALTER COLUMN is_active SET NOT NULL;

ALTER TABLE users
    ALTER COLUMN code SET NOT NULL;
