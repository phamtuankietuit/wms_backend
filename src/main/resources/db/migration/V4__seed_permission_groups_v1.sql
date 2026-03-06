INSERT INTO permission_groups (id, created_at, updated_at, deleted_at, code, name)
VALUES
    (
        gen_random_uuid(),
        NOW(),
        NOW(),
        NULL,
        'USER',
        'User Management'
    ),
    (
        gen_random_uuid(),
        NOW(),
        NOW(),
        NULL,
        'ROLE',
        'Role Management'
    ),
    (
        gen_random_uuid(),
        NOW(),
        NOW(),
        NULL,
        'PERMISSION',
        'Permission Management'
    ),
    (
        gen_random_uuid(),
        NOW(),
        NOW(),
        NULL,
        'PERMISSION_GROUP',
        'Permission Group Management'
    ),
    (
        gen_random_uuid(),
        NOW(),
        NOW(),
        NULL,
        'WAREHOUSE',
        'Warehouse Management'
    ),
    (
        gen_random_uuid(),
        NOW(),
        NOW(),
        NULL,
        'USER_WAREHOUSE',
        'User Warehouse Management'
    )
ON CONFLICT (code)
DO UPDATE SET
    name = EXCLUDED.name,
    updated_at = NOW(),
    deleted_at = NULL;