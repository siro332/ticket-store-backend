-- Users
INSERT INTO users (id, created_at, email, full_name, password_hash, phone, status, updated_at) VALUES
(UNHEX(REPLACE('1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d', '-', '')), NOW(), 'admin@example.com', 'Admin User', '$2a$10$N2G/J2ZlD.q.H0Y.B8S2O.v7L0K9Y.X9X9X9X9X9X9X9X9X9X9X9X9X9X9X9X9X9', '1112223333', 'ACTIVE', NOW()),
(UNHEX(REPLACE('2a3b4c5d-6e7f-8a9b-0c1d-2e3f4a5b6c7d', '-', '')), NOW(), 'organizer@example.com', 'Organizer User', '$2a$10$N2G/J2ZlD.q.H0Y.B8S2O.v7L0K9Y.X9X9X9X9X9X9X9X9X9X9X9X9X9X9X9X9X9', '4445556666', 'ACTIVE', NOW()),
(UNHEX(REPLACE('3a4b5c6d-7e8f-9a0b-1c2d-3e4f5a6b7c8d', '-', '')), NOW(), 'user@example.com', 'Regular User', '$2a$10$N2G/J2ZlD.q.H0Y.B8S2O.v7L0K9Y.X9X9X9X9X9X9X9X9X9X9X9X9X9X9X9X9X9', '7778889999', 'ACTIVE', NOW())
ON DUPLICATE KEY UPDATE
    created_at=VALUES(created_at), email=VALUES(email), full_name=VALUES(full_name), password_hash=VALUES(password_hash), phone=VALUES(phone), status=VALUES(status), updated_at=VALUES(updated_at);

-- Roles
INSERT INTO roles (id, description, name) VALUES
(1, 'Administrator role with full access', 'ADMIN'),
(2, 'Event organizer role', 'ORGANIZER'),
(3, 'Staff role for event check-in', 'STAFF'),
(4, 'Regular user role', 'USER')
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), description=VALUES(description), name=VALUES(name);

-- Permissions
INSERT INTO permissions (id, description, name) VALUES
(1, 'Can manage all system settings', 'MANAGE_ALL'),
(2, 'Can create and manage events', 'MANAGE_EVENTS'),
(3, 'Can check-in attendees', 'CHECK_IN_TICKETS'),
(4, 'Can view own orders and tickets', 'VIEW_OWN_DATA')
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), description=VALUES(description), name=VALUES(name);

-- Role Permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
(1, 1), -- ADMIN has MANAGE_ALL
(2, 2), -- ORGANIZER has MANAGE_EVENTS
(3, 3), -- STAFF has CHECK_IN_TICKETS
(4, 4) -- USER has VIEW_OWN_DATA
ON DUPLICATE KEY UPDATE
    role_id=VALUES(role_id), permission_id=VALUES(permission_id);

-- Organizations
INSERT INTO organizations (id, cancellation_policy, created_at, fees_and_taxes, name, refund_policy, status, supported_payment_methods, updated_at, owner_user_id, contact_email, description) VALUES
(1, 'Standard cancellation policy', NOW(), '5% tax, 2% fee', 'Global Events Inc.', 'Standard refund policy', 'ACTIVE', 'Credit Card,PayPal', NOW(), UNHEX(REPLACE('2a3b4c5d-6e7f-8a9b-0c1d-2e3f4a5b6c7d', '-', '')), 'contact@globalevents.com', 'A global event management company.')
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), cancellation_policy=VALUES(cancellation_policy), created_at=VALUES(created_at), fees_and_taxes=VALUES(fees_and_taxes), name=VALUES(name), refund_policy=VALUES(refund_policy), status=VALUES(status), supported_payment_methods=VALUES(supported_payment_methods), updated_at=VALUES(updated_at), owner_user_id=VALUES(owner_user_id), contact_email=VALUES(contact_email), description=VALUES(description);

-- User Organization Roles
INSERT INTO user_organization_roles (id, created_at, updated_at, organization_id, role_id, user_id) VALUES
(1, NOW(), NOW(), 1, 2, UNHEX(REPLACE('2a3b4c5d-6e7f-8a9b-0c1d-2e3f4a5b6c7d', '-', ''))), -- Organizer User is ORGANIZER of Global Events Inc.
(2, NOW(), NOW(), 1, 3, UNHEX(REPLACE('3a4b5c6d-7e8f-9a0b-1c2d-3e4f5a6b7c8d', '-', ''))) -- Regular User is STAFF of Global Events Inc. (for testing check-in)
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), created_at=VALUES(created_at), updated_at=VALUES(updated_at), organization_id=VALUES(organization_id), role_id=VALUES(role_id), user_id=VALUES(user_id);

-- Venues
INSERT INTO venues (id, address, capacity, city, map_image, name) VALUES
(1, '123 Main St', 5000, 'Ho Chi Minh City', 'http://example.com/map1.png', 'Grand Arena'),
(2, '456 Side St', 500, 'Hanoi', 'http://example.com/map2.png', 'Cozy Club')
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), address=VALUES(address), capacity=VALUES(capacity), city=VALUES(city), map_image=VALUES(map_image), name=VALUES(name);

-- Events
INSERT INTO events (id, allow_attendee_name_change, allow_ticket_transfer, buyer_count, category, cover_image, created_at, description, end_time, format, name, organizer_id, refund_deadline_hours, refund_enabled, refund_fee_percent, seat_configuration, start_time, status, updated_at, venue_id) VALUES
(1, 1, 1, 100, 'Music', 'http://example.com/event1.jpg', NOW(), 'A fantastic music concert.', '2026-01-15 22:00:00', 'Concert', 'New Year Concert', UNHEX(REPLACE('2a3b4c5d-6e7f-8a9b-0c1d-2e3f4a5b6c7d', '-', '')), 24, 1, 0.10, '{"zones": ["VIP", "General"]}', '2026-01-15 19:00:00', 'PUBLISHED', NOW(), 1),
(2, 0, 0, 50, 'Sports', 'http://example.com/event2.jpg', NOW(), 'An exciting football match.', '2026-02-20 18:00:00', 'Match', 'Football Mania', UNHEX(REPLACE('2a3b4c5d-6e7f-8a9b-0c1d-2e3f4a5b6c7d', '-', '')), 48, 1, 0.05, '{}', '2026-02-20 16:00:00', 'PUBLISHED', NOW(), 1),
(3, 1, 1, 0, 'Conferences', 'http://example.com/event3.jpg', NOW(), 'Annual Tech Conference.', '2026-03-10 17:00:00', 'Conference', 'Tech Summit 2026', UNHEX(REPLACE('2a3b4c5d-6e7f-8a9b-0c1d-2e3f4a5b6c7d', '-', '')), 72, 1, 0.00, '{}', '2026-03-10 09:00:00', 'PENDING', NOW(), 2)
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), allow_attendee_name_change=VALUES(allow_attendee_name_change), allow_ticket_transfer=VALUES(allow_ticket_transfer), buyer_count=VALUES(buyer_count), category=VALUES(category), cover_image=VALUES(cover_image), created_at=VALUES(created_at), description=VALUES(description), end_time=VALUES(end_time), format=VALUES(format), name=VALUES(name), organizer_id=VALUES(organizer_id), refund_deadline_hours=VALUES(refund_deadline_hours), refund_enabled=VALUES(refund_enabled), refund_fee_percent=VALUES(refund_fee_percent), seat_configuration=VALUES(seat_configuration), start_time=VALUES(start_time), status=VALUES(status), updated_at=VALUES(updated_at), venue_id=VALUES(venue_id);

-- Ticket Types
INSERT INTO ticket_types (id, end_sale, name, price, purchase_limit, quota, start_sale, event_id) VALUES
(1, '2026-01-14 23:59:59', 'VIP Ticket', 100.00, 5, 50, NOW(), 1),
(2, '2026-01-14 23:59:59', 'General Admission', 50.00, 10, 200, NOW(), 1),
(3, '2026-02-19 23:59:59', 'Standard Pass', 75.00, 3, 100, NOW(), 2)
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), end_sale=VALUES(end_sale), name=VALUES(name), price=VALUES(price), purchase_limit=VALUES(purchase_limit), quota=VALUES(quota), start_sale=VALUES(start_sale), event_id=VALUES(event_id);

-- Discounts
INSERT INTO discounts (id, code, discount_amount, discount_percent, minimum_order_amount, usage_limit, used_count, valid_from, valid_to, event_id) VALUES
(1, 'EARLYBIRD', NULL, 15, 50.00, 100, 5, NOW(), '2025-12-31 23:59:59', 1),
(2, 'SAVE10', 10.00, NULL, 30.00, 50, 2, NOW(), '2026-01-31 23:59:59', 1)
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), code=VALUES(code), discount_amount=VALUES(discount_amount), discount_percent=VALUES(discount_percent), minimum_order_amount=VALUES(minimum_order_amount), usage_limit=VALUES(usage_limit), used_count=VALUES(used_count), valid_from=VALUES(valid_from), valid_to=VALUES(valid_to), event_id=VALUES(event_id);

-- Seats (for Event 1)
INSERT INTO seats (id, is_available, locked, row_label, seat_category, seat_number, section, event_id, ticket_type_id) VALUES
(1, 1, 0, 'A', 'VIP', '1', 'Main', 1, 1),
(2, 1, 0, 'A', 'VIP', '2', 'Main', 1, 1),
(3, 1, 0, 'B', 'General', '10', 'Main', 1, 2)
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), is_available=VALUES(is_available), locked=VALUES(locked), row_label=VALUES(row_label), seat_category=VALUES(seat_category), seat_number=VALUES(seat_number), section=VALUES(section), event_id=VALUES(event_id), ticket_type_id=VALUES(ticket_type_id);

-- Orders
INSERT INTO orders (id, created_at, currency, discount_code, event_id, is_resale, payment_method, status, total_amount, updated_at, user_id) VALUES
(1, NOW(), 'USD', NULL, 1, 0, 'Credit Card', 'PAID', 100.00, NOW(), UNHEX(REPLACE('3a4b5c6d-7e8f-9a0b-1c2d-3e4f5a6b7c8d', '-', '')))
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), created_at=VALUES(created_at), currency=VALUES(currency), discount_code=VALUES(discount_code), event_id=VALUES(event_id), is_resale=VALUES(is_resale), payment_method=VALUES(payment_method), status=VALUES(status), total_amount=VALUES(total_amount), updated_at=VALUES(updated_at), user_id=VALUES(user_id);

-- Order Items (for Order 1, General Admission tickets for Event 1)
INSERT INTO order_items (id, price, quantity, ticket_code, ticket_type_id, order_id) VALUES
(1, 50.00, 2, NULL, 2, 1) -- 2 General Admission tickets at $50 each
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), price=VALUES(price), quantity=VALUES(quantity), ticket_code=VALUES(ticket_code), ticket_type_id=VALUES(ticket_type_id), order_id=VALUES(order_id);

-- Payment Info (for Order 1)
INSERT INTO payment_info (id, amount, method, paid_at, status, transaction_id, order_id) VALUES
(1, 100.00, 'Credit Card', NOW(), 'SUCCESS', 'TX123456789', 1)
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), amount=VALUES(amount), method=VALUES(method), paid_at=VALUES(paid_at), status=VALUES(status), transaction_id=VALUES(transaction_id), order_id=VALUES(order_id);

-- Payment Transactions (for Order 1, simulating VNPay success)
INSERT INTO payment_transactions (id, amount, created_at, order_id, payment_method, status, transaction_id, updated_at, vnpay_txn_ref) VALUES
(1, 100.00, NOW(), 1, 'VNPAY', 'SUCCESS', 'VNPAY_TX_SUCCESS_1', NOW(), 'VNPAYREF123')
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), amount=VALUES(amount), created_at=VALUES(created_at), order_id=VALUES(order_id), payment_method=VALUES(payment_method), status=VALUES(status), transaction_id=VALUES(transaction_id), updated_at=VALUES(updated_at), vnpay_txn_ref=VALUES(vnpay_txn_ref);

-- Tickets (2 tickets for Order Item 1)
INSERT INTO tickets (id, attendee_email, attendee_name, created_at, seat_id, status, ticket_code, updated_at, order_item_id) VALUES
(1, 'user@example.com', 'Regular User', NOW(), NULL, 'ISSUED', 'TICKET-CODE-001', NOW(), 1),
(2, 'user@example.com', 'Regular User', NOW(), NULL, 'ISSUED', 'TICKET-CODE-002', NOW(), 1)
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), attendee_email=VALUES(attendee_email), attendee_name=VALUES(attendee_name), created_at=VALUES(created_at), seat_id=VALUES(seat_id), status=VALUES(status), ticket_code=VALUES(ticket_code), updated_at=VALUES(updated_at), order_item_id=VALUES(order_item_id);

-- Check-in Logs (for Ticket 1)
INSERT INTO check_in_logs (id, check_in_time, device_id, event_id, gate, user_id, ticket_id) VALUES
(1, NOW(), 'MOBILE_SCANNER_01', 1, 'GATE_A', UNHEX(REPLACE('3a4b5c6d-7e8f-9a0b-1c2d-3e4f5a6b7c8d', '-', '')), 1)
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), check_in_time=VALUES(check_in_time), device_id=VALUES(device_id), event_id=VALUES(event_id), gate=VALUES(gate), user_id=VALUES(user_id), ticket_id=VALUES(ticket_id);

-- Reservations (optional, for seated events or pending carts, will add an example for event 2)
INSERT INTO reservations (id, event_id, expire_at, quantity, seat_id, status, ticket_type_id, user_id) VALUES
(1, 2, '2026-02-19 15:00:00', 1, NULL, 'PENDING', 3, UNHEX(REPLACE('3a4b5c6d-7e8f-9a0b-1c2d-3e4f5a6b7c8d', '-', '')))
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), event_id=VALUES(event_id), expire_at=VALUES(expire_at), quantity=VALUES(quantity), seat_id=VALUES(seat_id), status=VALUES(status), ticket_type_id=VALUES(ticket_type_id), user_id=VALUES(user_id);

-- Marketplace Listings (Ticket 2 for sale by Regular User)
INSERT INTO marketplace_listings (id, created_at, price, seller_id, status, updated_at, ticket_id) VALUES
(UNHEX(REPLACE('5a6b7c8d-9e0f-1a2b-3c4d-5e6f7a8b9c0d', '-', '')), NOW(), 60.00, UNHEX(REPLACE('3a4b5c6d-7e8f-9a0b-1c2d-3e4f5a6b7c8d', '-', '')), 'ACTIVE', NOW(), 2)
ON DUPLICATE KEY UPDATE
    id=LAST_INSERT_ID(id), created_at=VALUES(created_at), price=VALUES(price), seller_id=VALUES(seller_id), status=VALUES(status), updated_at=VALUES(updated_at), ticket_id=VALUES(ticket_id);

-- Ticket Transfers
INSERT INTO ticket_transfers (id, created_at, recipient_email, sender_id, status, updated_at, ticket_id) VALUES
(UNHEX(REPLACE('6a7b8c9d-0e1f-2a3b-4c5d-6e7f8a9b0c1d', '-', '')), NOW(), 'newuser@example.com', UNHEX(REPLACE('3a4b5c6d-7e8f-9a0b-1c2d-3e4f5a6b7c8d', '-', '')), 'PENDING', NOW(), 1)
ON DUPLICATE KEY UPDATE
    id=VALUES(id), created_at=VALUES(created_at), recipient_email=VALUES(recipient_email), sender_id=VALUES(sender_id), status=VALUES(status), updated_at=VALUES(updated_at), ticket_id=VALUES(ticket_id);