# Ticket Store - Comprehensive Test Scenarios (End-to-End)

This document provides a step-by-step guide to testing the Ticket Store application, covering Frontend UI interactions, Backend API verifications, and Database state checks.

## Prerequisites

1.  **Services Running:** All microservices (Auth, Event, Order, Payment) and Frontend.
2.  **Database Populated:** Run `seed_data.sql`.
3.  **Tools:** Browser (Chrome/Firefox), Terminal (for `curl` and SQL).

---

## 1. System Health Check

**Goal:** Ensure all backend services are reachable.

*   **Auth Service:** `curl http://localhost:8081/actuator/health` -> `{"status":"UP"}`
*   **Event Service:** `curl http://localhost:8082/actuator/health` -> `{"status":"UP"}`
*   **Order Service:** `curl http://localhost:8083/actuator/health` -> `{"status":"UP"}`
*   **Payment Service:** `curl http://localhost:8084/actuator/health` -> `{"status":"UP"}`

---

## 2. Attendee Flow: Registration to Purchase

**Goal:** A new user registers, finds an event, and buys a ticket.

### Step 2.1: Registration
*   **UI Action:** Go to `/register`. Sign up as `newuser@test.com` / `password` / `New User`.
*   **UI Result:** Redirects to Home. "Hi, New" displayed in navbar.
*   **Backend Check (SQL):**
    ```sql
    SELECT email, full_name FROM users WHERE email = 'newuser@test.com';
    -- Expected: 1 row returned.
    ```

### Step 2.2: Search Event
*   **UI Action:** Go to `/events`. Search for "Rock".
*   **UI Result:** "Summer Rock Fest 2025" card appears.
*   **Backend Logic:** `GET /api/events/search?keyword=Rock` called.

### Step 2.3: Add to Cart
*   **UI Action:** Click "View Details". Select "General Admission", Qty: 2. Click "Add to Cart".
*   **UI Result:** Redirects to `/cart`. Item listed with correct price ($50.00 * 2 = $100.00).
*   **Backend Logic:** Frontend manages cart in `localStorage` (or backend session if implemented server-side). Currently client-side `CartContext`.

### Step 2.4: Checkout
*   **UI Action:** Click "Proceed to Checkout". Enter Discount `EARLYBIRD25`. Click "Apply".
*   **UI Result:** Total reduces by 10% ($100 -> $90). "Discount applied" success toast.
*   **Backend Check (Network Tab):** `GET /api/events/{id}/discounts/validate?code=EARLYBIRD25` returns 200 OK and discount details.

### Step 2.5: Place Order
*   **UI Action:** Select "Credit Card". Click "Place Order & Pay".
*   **UI Result:** Loading spinner -> Redirect to `/order-confirmation/{id}`. Success message.
*   **Backend Check (SQL):**
    ```sql
    SELECT * FROM orders WHERE user_id = (SELECT id FROM users WHERE email = 'newuser@test.com') ORDER BY created_at DESC LIMIT 1;
    -- Expected: status = 'PAID', total_amount = 90.00.
    ```
*   **Backend Check (API):**
    ```bash
    # Login to get token (replace with actual call)
    TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login -H "Content-Type: application/json" -d '{"email":"newuser@test.com","password":"password"}' | jq -r .accessToken)
    # Get Orders
    curl -H "Authorization: Bearer $TOKEN" http://localhost:8083/api/orders/user/{USER_UUID}
    ```

---

## 3. Organizer Flow: Event Management

**Goal:** Organizer creates a new event and checks sales.

### Step 3.1: Login
*   **UI Action:** Login as `organizer@example.com` / `password`.
*   **UI Result:** Navbar shows "Organizer" button.

### Step 3.2: Create Event
*   **UI Action:** Go to Organizer Dashboard -> "New Event".
    *   Name: `Tech Meetup 2025`
    *   Category: `Networking`
    *   Venue: `Innovation Hub`
    *   Capacity: `100`
    *   Start/End: Future dates.
*   **UI Result:** Success toast. Redirect to Dashboard. Event listed.
*   **Backend Check (SQL):**
    ```sql
    SELECT * FROM events WHERE name = 'Tech Meetup 2025';
    -- Expected: 1 row.
    ```

### Step 3.3: Add Ticket Types
*   **UI Action:** Edit `Tech Meetup 2025`. Scroll to "Ticket Types".
    *   Name: `Free Entry`
    *   Price: `0`
    *   Quota: `50`
    *   Click "Add".
*   **UI Result:** Ticket type appears in list.
*   **Backend Check (SQL):**
    ```sql
    SELECT * FROM ticket_types WHERE name = 'Free Entry';
    -- Expected: 1 row linked to the event.
    ```

### Step 3.4: View Reports
*   **UI Action:** Go to "Reports". Select "Summer Rock Fest 2025" (from Scenario 2).
*   **UI Result:** Total Revenue shows `$90.00` (from User's purchase). Tickets Sold: `2`.
*   **Backend Logic:** `GET /api/reports/revenue/event/{id}`.

---

## 4. Staff Flow: Check-in

**Goal:** Staff checks in the user from Scenario 2.

### Step 4.1: Login
*   **UI Action:** Login as `staff@example.com` / `password`.
*   **UI Result:** Navbar shows "Check-in".

### Step 4.2: Find Attendee
*   **UI Action:** Go to "Check-in". Select "Summer Rock Fest 2025".
*   **UI Result:** Attendee list loads. Find `New User`. Status: "Pending".

### Step 4.3: Check-in
*   **UI Action:** Click "Manual" check-in button for `New User` (or enter their Ticket Code).
*   **UI Result:** Success toast. Status changes to "Checked In".
*   **Backend Check (SQL):**
    ```sql
    SELECT * FROM tickets WHERE attendee_email = 'newuser@test.com';
    -- Expected: status = 'SCANNED'.
    SELECT * FROM check_in_logs;
    -- Expected: New entry for this ticket.
    ```

---

## 5. Admin Flow: Global Config

**Goal:** Admin changes system fees.

### Step 5.1: Login
*   **UI Action:** Login as `admin@example.com` / `password`.
*   **UI Result:** Navbar shows "Admin".

### Step 5.2: Change Fees
*   **UI Action:** Go to Admin Dashboard. Scroll to "Global Fees". Change Tax Rate to `10%`. Click Save.
*   **UI Result:** Success toast.
*   **Note:** Since this is a frontend-only demo in the current prototype, database state won't change unless the specific Admin API is fully implemented. Verify UI persistence or console logs.

---

## 6. Edge Cases & Error Handling

*   **Invalid Login:** Try logging in with `wrong@test.com`. -> Error toast "Invalid credentials".
*   **Sold Out:** Try adding more tickets than available quota. -> "Add to Cart" button disabled or error toast.
*   **Expired Discount:** Try applying a code past its valid date. -> Error toast "Invalid or expired discount".
*   **Unauthorized Access:** Try accessing `/admin/dashboard` as `newuser@test.com`. -> Redirect to Home or "Access Denied".