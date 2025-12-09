INSERT IGNORE INTO roles (name, description) VALUES
('ADMIN', 'System administrator'),
('ORGANIZER', 'Event organizer'),
('STAFF', 'Event staff'),
('USER', 'Regular user');

-- Users
-- Admin (admin@example.com)
INSERT INTO users (id, email, password_hash, full_name, status, created_at, updated_at)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', '')), 'admin@example.com', '$2a$10$Q2iCg8bB.j0J8M7X.3F4U5P6O7N8M9L0K1J2H3G4F5E6D7C8B9A0Z.', 'System Admin', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@example.com');

-- Organizer (organizer@example.com)
INSERT INTO users (id, email, password_hash, full_name, status, created_at, updated_at)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000002', '-', '')), 'organizer@example.com', '$2a$10$Q2iCg8bB.j0J8M7X.3F4U5P6O7N8M9L0K1J2H3G4F5E6D7C8B9A0Z.', 'Event Organizer', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'organizer@example.com');

-- Staff (staff@example.com)
INSERT INTO users (id, email, password_hash, full_name, status, created_at, updated_at)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000003', '-', '')), 'staff@example.com', '$2a$10$Q2iCg8bB.j0J8M7X.3F4U5P6O7N8M9L0K1J2H3G4F5E6D7C8B9A0Z.', 'Gate Staff', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'staff@example.com');

-- User (user@example.com)
INSERT INTO users (id, email, password_hash, full_name, status, created_at, updated_at)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000004', '-', '')), 'user@example.com', '$2a$10$Q2iCg8bB.j0J8M7X.3F4U5P6O7N8M9L0K1J2H3G4F5E6D7C8B9A0Z.', 'John Doe', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user@example.com');

-- Alice (alice@test.com)
INSERT INTO users (id, email, password_hash, full_name, status, created_at, updated_at)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000005', '-', '')), 'alice@test.com', '$2a$10$Q2iCg8bB.j0J8M7X.3F4U5P6O7N8M9L0K1J2H3G4F5E6D7C8B9A0Z.', 'Alice Wonderland', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'alice@test.com');

-- Bob (bob@test.com)
INSERT INTO users (id, email, password_hash, full_name, status, created_at, updated_at)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', '')), 'bob@test.com', '$2a$10$Q2iCg8bB.j0J8M7X.3F4U5P6O7N8M9L0K1J2H3G4F5E6D7C8B9A0Z.', 'Bob Builder', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'bob@test.com');

-- Organizations
INSERT INTO organizations (id, name, description, contact_email, owner_user_id, status, created_at, updated_at)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-00000000000a', '-', '')), 'Global Admin Org', 'Organization for system administrators', 'admin@example.com', UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', '')), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM organizations WHERE id = UNHEX(REPLACE('00000000-0000-0000-0000-00000000000a', '-', '')));

INSERT INTO organizations (id, name, description, contact_email, owner_user_id, status, created_at, updated_at)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-00000000000b', '-', '')), 'Best Events Co.', 'We organize the best events.', 'contact@bestevents.com', UNHEX(REPLACE('00000000-0000-0000-0000-000000000002', '-', '')), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM organizations WHERE id = UNHEX(REPLACE('00000000-0000-0000-0000-00000000000b', '-', '')));

-- User Organization Roles
INSERT INTO user_organization_roles (user_id, organization_id, role_id)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', '')), UNHEX(REPLACE('00000000-0000-0000-0000-00000000000a', '-', '')), (SELECT id FROM roles WHERE name = 'ADMIN')
WHERE NOT EXISTS (SELECT 1 FROM user_organization_roles WHERE user_id = UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', '')) AND organization_id = UNHEX(REPLACE('00000000-0000-0000-0000-00000000000a', '-', '')));

INSERT INTO user_organization_roles (user_id, organization_id, role_id)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000002', '-', '')), UNHEX(REPLACE('00000000-0000-0000-0000-00000000000b', '-', '')), (SELECT id FROM roles WHERE name = 'ORGANIZER')
WHERE NOT EXISTS (SELECT 1 FROM user_organization_roles WHERE user_id = UNHEX(REPLACE('00000000-0000-0000-0000-000000000002', '-', '')) AND organization_id = UNHEX(REPLACE('00000000-0000-0000-0000-00000000000b', '-', '')));

INSERT INTO user_organization_roles (user_id, organization_id, role_id)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000003', '-', '')), UNHEX(REPLACE('00000000-0000-0000-0000-00000000000b', '-', '')), (SELECT id FROM roles WHERE name = 'STAFF')
WHERE NOT EXISTS (SELECT 1 FROM user_organization_roles WHERE user_id = UNHEX(REPLACE('00000000-0000-0000-0000-000000000003', '-', '')) AND organization_id = UNHEX(REPLACE('00000000-0000-0000-0000-00000000000b', '-', '')));
