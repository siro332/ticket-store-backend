INSERT IGNORE INTO roles (name, description) VALUES
('ADMIN', 'System administrator'),
('ORGANIZER', 'Event organizer'),
('STAFF', 'Event staff'),
('USER', 'Regular user');

-- Insert default ADMIN user
INSERT INTO users (id, email, password_hash, full_name, status, created_at, updated_at)
SELECT * FROM (SELECT
    UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', '')) AS id,
    'admin@example.com' AS email,
    '$2a$10$7UQX1Q7AOZT2xZQ/FSR7LevUDz1GaiKwvjt4upHp8SPrtUxSuMux6' AS password_hash, -- 'admin123'
    'System Admin' AS full_name,
    'ACTIVE' AS status,
    NOW() AS created_at,
    NOW() AS updated_at
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', '')));

-- Insert default Admin Organization
INSERT INTO organizations (id, name, description, contact_email, owner_id, status, created_at, updated_at)
SELECT * FROM (SELECT
    UNHEX(REPLACE('00000000-0000-0000-0000-00000000000a', '-', '')) AS id,
    'Global Admin Org' AS name,
    'Organization for system administrators' AS description,
    'admin@example.com' AS contact_email,
    UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', '')) AS owner_id,
    'ACTIVE' AS status,
    NOW() AS created_at,
    NOW() AS updated_at
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM organizations WHERE id = UNHEX(REPLACE('00000000-0000-0000-0000-00000000000a', '-', '')));

-- Assign ADMIN role to the Admin user within the Admin Organization
INSERT INTO user_organization_roles (user_id, organization_id, role_id)
SELECT * FROM (SELECT
    UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', '')) AS user_id,
    UNHEX(REPLACE('00000000-0000-0000-0000-00000000000a', '-', '')) AS organization_id,
    (SELECT id FROM roles WHERE name = 'ADMIN') AS role_id
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM user_organization_roles WHERE user_id = UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', '')) AND organization_id = UNHEX(REPLACE('00000000-0000-0000-0000-00000000000a', '-', '')));