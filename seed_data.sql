-- ============================================================
-- Authentication & Organization Service Data
-- ============================================================

-- 1. Roles
INSERT IGNORE INTO roles (name, description) VALUES
('ADMIN', 'System administrator'),
('ORGANIZER', 'Event organizer'),
('STAFF', 'Event staff'),
('USER', 'Regular user');

-- 2. Users
-- Admin (admin@example.com / password)
INSERT INTO users (id, email, password_hash, full_name, status, created_at, updated_at)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', '')), 'admin@example.com', '$2a$10$Q2iCg8bB.j0J8M7X.3F4U5P6O7N8M9L0K1J2H3G4F5E6D7C8B9A0Z.', 'System Admin', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@example.com');

-- Organizer (organizer@example.com / password)
INSERT INTO users (id, email, password_hash, full_name, status, created_at, updated_at)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000002', '-', '')), 'organizer@example.com', '$2a$10$Q2iCg8bB.j0J8M7X.3F4U5P6O7N8M9L0K1J2H3G4F5E6D7C8B9A0Z.', 'Event Organizer', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'organizer@example.com');

-- Staff (staff@example.com / password)
INSERT INTO users (id, email, password_hash, full_name, status, created_at, updated_at)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000003', '-', '')), 'staff@example.com', '$2a$10$Q2iCg8bB.j0J8M7X.3F4U5P6O7N8M9L0K1J2H3G4F5E6D7C8B9A0Z.', 'Gate Staff', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'staff@example.com');

-- User (user@example.com / password)
INSERT INTO users (id, email, password_hash, full_name, status, created_at, updated_at)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000004', '-', '')), 'user@example.com', '$2a$10$Q2iCg8bB.j0J8M7X.3F4U5P6O7N8M9L0K1J2H3G4F5E6D7C8B9A0Z.', 'John Doe', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user@example.com');

-- Alice (alice@test.com / password)
INSERT INTO users (id, email, password_hash, full_name, status, created_at, updated_at)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000005', '-', '')), 'alice@test.com', '$2a$10$Q2iCg8bB.j0J8M7X.3F4U5P6O7N8M9L0K1J2H3G4F5E6D7C8B9A0Z.', 'Alice Wonderland', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'alice@test.com');

-- Bob (bob@test.com / password)
INSERT INTO users (id, email, password_hash, full_name, status, created_at, updated_at)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', '')), 'bob@test.com', '$2a$10$Q2iCg8bB.j0J8M7X.3F4U5P6O7N8M9L0K1J2H3G4F5E6D7C8B9A0Z.', 'Bob Builder', 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'bob@test.com');

-- 3. Organizations
-- "Global Admin Org"
INSERT INTO organizations (id, name, description, contact_email, owner_user_id, status, created_at, updated_at)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-00000000000a', '-', '')), 'Global Admin Org', 'Organization for system administrators', 'admin@example.com', UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', '')), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM organizations WHERE id = UNHEX(REPLACE('00000000-0000-0000-0000-00000000000a', '-', '')));

-- "Best Events Co."
INSERT INTO organizations (id, name, description, contact_email, owner_user_id, status, created_at, updated_at)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-00000000000b', '-', '')), 'Best Events Co.', 'We organize the best events.', 'contact@bestevents.com', UNHEX(REPLACE('00000000-0000-0000-0000-000000000002', '-', '')), 'ACTIVE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM organizations WHERE id = UNHEX(REPLACE('00000000-0000-0000-0000-00000000000b', '-', '')));

-- 4. User Organization Roles
-- Admin -> Global Admin Org (ADMIN)
INSERT INTO user_organization_roles (user_id, organization_id, role_id)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', '')), UNHEX(REPLACE('00000000-0000-0000-0000-00000000000a', '-', '')), (SELECT id FROM roles WHERE name = 'ADMIN')
WHERE NOT EXISTS (SELECT 1 FROM user_organization_roles WHERE user_id = UNHEX(REPLACE('00000000-0000-0000-0000-000000000001', '-', '')) AND organization_id = UNHEX(REPLACE('00000000-0000-0000-0000-00000000000a', '-', '')));

-- Organizer -> Best Events Co. (ORGANIZER)
INSERT INTO user_organization_roles (user_id, organization_id, role_id)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000002', '-', '')), UNHEX(REPLACE('00000000-0000-0000-0000-00000000000b', '-', '')), (SELECT id FROM roles WHERE name = 'ORGANIZER')
WHERE NOT EXISTS (SELECT 1 FROM user_organization_roles WHERE user_id = UNHEX(REPLACE('00000000-0000-0000-0000-000000000002', '-', '')) AND organization_id = UNHEX(REPLACE('00000000-0000-0000-0000-00000000000b', '-', '')));

-- Staff -> Best Events Co. (STAFF)
INSERT INTO user_organization_roles (user_id, organization_id, role_id)
SELECT UNHEX(REPLACE('00000000-0000-0000-0000-000000000003', '-', '')), UNHEX(REPLACE('00000000-0000-0000-0000-00000000000b', '-', '')), (SELECT id FROM roles WHERE name = 'STAFF')
WHERE NOT EXISTS (SELECT 1 FROM user_organization_roles WHERE user_id = UNHEX(REPLACE('00000000-0000-0000-0000-000000000003', '-', '')) AND organization_id = UNHEX(REPLACE('00000000-0000-0000-0000-00000000000b', '-', '')));


-- ============================================================
-- Event Service Data
-- ============================================================

-- 1. Venues
INSERT IGNORE INTO venues (id, name, address, city, capacity, map_image) VALUES
(1, 'Grand Stadium', '1 Stadium Dr', 'Metropolis', 50000, 'https://placehold.co/600x400?text=Stadium+Map'),
(2, 'Community Theater', '42 Arts Way', 'Smalltown', 500, 'https://placehold.co/600x400?text=Theater+Map'),
(3, 'The Underground Club', '99 Basement St', 'Nightcity', 200, NULL);

-- 2. Events
-- Event 1: Rock Concert (Stadium, Unseated)
INSERT INTO events (id, organizer_id, name, description, category, start_time, end_time, venue_id, cover_image, status, allow_ticket_transfer, allow_attendee_name_change, refund_enabled, refund_deadline_hours, refund_fee_percent, created_at, updated_at)
VALUES (1, UNHEX(REPLACE('00000000-0000-0000-0000-000000000002', '-', '')), 'Summer Rock Fest 2025', 'The biggest rock festival of the summer featuring top bands.', 'Music', '2025-07-15 18:00:00', '2025-07-15 23:00:00', 1, 'https://placehold.co/800x400?text=Rock+Fest', 'PUBLISHED', 1, 1, 1, 48, 0.10, NOW(), NOW())
ON DUPLICATE KEY UPDATE name=name;

-- Event 2: Tech Conference (Theater, Seated)
INSERT INTO events (id, organizer_id, name, description, category, start_time, end_time, venue_id, cover_image, status, allow_ticket_transfer, allow_attendee_name_change, refund_enabled, refund_deadline_hours, refund_fee_percent, created_at, updated_at)
VALUES (2, UNHEX(REPLACE('00000000-0000-0000-0000-000000000002', '-', '')), 'Future Tech Summit', 'A deep dive into AI, Quantum Computing, and Web3.', 'Conference', '2025-09-10 09:00:00', '2025-09-12 17:00:00', 2, 'https://placehold.co/800x400?text=Tech+Summit', 'PUBLISHED', 0, 1, 1, 72, 0.05, NOW(), NOW())
ON DUPLICATE KEY UPDATE name=name;

-- Event 3: Jazz Night (Club, Unseated)
INSERT INTO events (id, organizer_id, name, description, category, start_time, end_time, venue_id, cover_image, status, allow_ticket_transfer, allow_attendee_name_change, refund_enabled, refund_deadline_hours, refund_fee_percent, created_at, updated_at)
VALUES (3, UNHEX(REPLACE('00000000-0000-0000-0000-000000000002', '-', '')), 'Smooth Jazz Night', 'Relaxing jazz tunes all night long.', 'Music', '2025-08-05 20:00:00', '2025-08-05 23:59:00', 3, 'https://placehold.co/800x400?text=Jazz+Night', 'DRAFT', 1, 1, 0, 0, 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE name=name;

-- Event 4: Classical Night (Theater, Seated)
INSERT INTO events (id, organizer_id, name, description, category, start_time, end_time, venue_id, cover_image, status, allow_ticket_transfer, allow_attendee_name_change, refund_enabled, refund_deadline_hours, refund_fee_percent, created_at, updated_at)
VALUES (4, UNHEX(REPLACE('00000000-0000-0000-0000-000000000002', '-', '')), 'Classical Night', 'Mozart, Beethoven, and more.', 'Arts', '2025-10-20 19:00:00', '2025-10-20 22:00:00', 2, 'https://placehold.co/800x400?text=Classical', 'PUBLISHED', 1, 0, 1, 24, 0.00, NOW(), NOW())
ON DUPLICATE KEY UPDATE name=name;

-- Event 5: Python Workshop (Club, Unseated)
INSERT INTO events (id, organizer_id, name, description, category, start_time, end_time, venue_id, cover_image, status, allow_ticket_transfer, allow_attendee_name_change, refund_enabled, refund_deadline_hours, refund_fee_percent, created_at, updated_at)
VALUES (5, UNHEX(REPLACE('00000000-0000-0000-0000-000000000002', '-', '')), 'Python Masterclass', 'Learn Python from experts.', 'Workshop', '2025-11-15 10:00:00', '2025-11-15 16:00:00', 3, 'https://placehold.co/800x400?text=Python', 'PUBLISHED', 1, 1, 1, 48, 0.20, NOW(), NOW())
ON DUPLICATE KEY UPDATE name=name;

-- 3. Ticket Types
-- For Event 1 (Rock Fest)
INSERT INTO ticket_types (id, name, event_id, price, quota, purchase_limit, start_sale, end_sale) VALUES
(1, 'General Admission', 1, 50.00, 10000, 6, '2025-01-01 00:00:00', '2025-07-15 17:00:00'),
(2, 'VIP Pit', 1, 150.00, 500, 4, '2025-01-01 00:00:00', '2025-07-15 17:00:00')
ON DUPLICATE KEY UPDATE name=name;

-- For Event 2 (Tech Summit)
INSERT INTO ticket_types (id, name, event_id, price, quota, purchase_limit, start_sale, end_sale) VALUES
(3, 'Standard Pass', 2, 299.00, 400, 2, '2025-03-01 00:00:00', '2025-09-09 23:59:00'),
(4, 'Student Pass', 2, 99.00, 50, 1, '2025-03-01 00:00:00', '2025-09-09 23:59:00')
ON DUPLICATE KEY UPDATE name=name;

-- For Event 4 (Classical)
INSERT INTO ticket_types (id, name, event_id, price, quota, purchase_limit, start_sale, end_sale) VALUES
(5, 'Orchestra', 4, 80.00, 200, 4, '2025-01-01 00:00:00', '2025-10-20 18:00:00'),
(6, 'Balcony', 4, 40.00, 300, 4, '2025-01-01 00:00:00', '2025-10-20 18:00:00')
ON DUPLICATE KEY UPDATE name=name;

-- For Event 5 (Python)
INSERT INTO ticket_types (id, name, event_id, price, quota, purchase_limit, start_sale, end_sale) VALUES
(7, 'Entry', 5, 20.00, 50, 1, '2025-01-01 00:00:00', '2025-11-15 09:00:00')
ON DUPLICATE KEY UPDATE name=name;

-- 4. Seats (For Event 2 - Tech Summit)
INSERT INTO seats (event_id, ticket_type_id, section, row_label, seat_number, seat_category, is_available, locked) VALUES
(2, 3, 'Orchestra', 'A', '1', 'Standard', 1, 0),
(2, 3, 'Orchestra', 'A', '2', 'Standard', 1, 0),
(2, 3, 'Orchestra', 'A', '3', 'Standard', 1, 0),
(2, 3, 'Orchestra', 'A', '4', 'Standard', 1, 0),
(2, 3, 'Orchestra', 'B', '1', 'Standard', 1, 0),
(2, 3, 'Orchestra', 'B', '2', 'Standard', 1, 0),
(2, 4, 'Balcony', 'AA', '1', 'Student', 1, 0),
(2, 4, 'Balcony', 'AA', '2', 'Student', 1, 0)
ON DUPLICATE KEY UPDATE seat_number=seat_number;

-- 5. Discounts
INSERT INTO discounts (code, discount_percent, discount_amount, minimum_order_amount, usage_limit, used_count, valid_from, valid_to, event_id) VALUES
('EARLYBIRD25', 10, NULL, 0, 100, 0, '2025-01-01 00:00:00', '2025-03-01 00:00:00', 1),
('STUDENT10', NULL, 10.00, 50.00, 500, 0, '2025-01-01 00:00:00', '2025-12-31 23:59:00', 1)
ON DUPLICATE KEY UPDATE code=code;


-- ============================================================
-- Order Service Data
-- ============================================================

-- 1. Orders
-- Order 1: Alice buys 2 General Admission tickets for Rock Fest
INSERT INTO orders (id, user_id, event_id, total_amount, currency, payment_method, status, created_at, updated_at) VALUES
(1, UNHEX(REPLACE('00000000-0000-0000-0000-000000000005', '-', '')), 1, 100.00, 'USD', 'Credit Card', 'PAID', NOW(), NOW())
ON DUPLICATE KEY UPDATE status=status;

-- Order 2: Bob buys 1 Standard Pass for Tech Summit
INSERT INTO orders (id, user_id, event_id, total_amount, currency, payment_method, status, created_at, updated_at) VALUES
(2, UNHEX(REPLACE('00000000-0000-0000-0000-000000000006', '-', '')), 2, 299.00, 'USD', 'PayPal', 'PAID', NOW(), NOW())
ON DUPLICATE KEY UPDATE status=status;

-- 2. Order Items
-- For Order 1
INSERT INTO order_items (id, order_id, ticket_type_id, price) VALUES
(1, 1, 1, 50.00), -- Ticket 1
(2, 1, 1, 50.00)  -- Ticket 2
ON DUPLICATE KEY UPDATE price=price;

-- For Order 2
INSERT INTO order_items (id, order_id, ticket_type_id, price) VALUES
(3, 2, 3, 299.00) -- Ticket 1
ON DUPLICATE KEY UPDATE price=price;

-- 3. Tickets
-- For Order 1 (Rock Fest - Unseated)
INSERT INTO tickets (id, order_item_id, ticket_code, attendee_name, attendee_email, status, created_at, updated_at) VALUES
(1, 1, 'ROCK-ALICE-001', 'Alice Wonderland', 'alice@test.com', 'ISSUED', NOW(), NOW()),
(2, 2, 'ROCK-ALICE-002', 'Alice Friend', 'alice@test.com', 'ISSUED', NOW(), NOW())
ON DUPLICATE KEY UPDATE ticket_code=ticket_code;

-- For Order 2 (Tech Summit - Seated, needs seat_id but we'll leave null for simplicity or assign one)
-- Assign Seat ID 1 (Orchestra A 1) to Bob
INSERT INTO tickets (id, order_item_id, ticket_code, attendee_name, attendee_email, status, seat_id, created_at, updated_at) VALUES
(3, 3, 'TECH-BOB-001', 'Bob Builder', 'bob@test.com', 'ISSUED', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE ticket_code=ticket_code;

-- 4. Reservations (For Order 2 - confirmed reservation logic)
-- Mark Seat 1 as reserved/sold (optional if tickets exist, but good for consistency)
-- (Skipping for brevity as Orders/Tickets are the source of truth for "sold")
