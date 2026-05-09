# Development Guide

## 1. Project Goal

This project is the 2026 CPT202 Group 1 XJTLU Academic Expert Appointment System.

It supports three user roles:

- Customer: XJTLU student or staff requester
- Specialist: XJTLU academic expert
- Administrator: system operator who manages academic areas, experts, bookings, and billing

The system provides registration, email verification, expert approval, expert search, availability management, booking review, booking status tracking, completion, feedback, and billing calculation.

## 2. Current Technology Stack

- Java 17+
- Spring Boot 4
- Spring Security
- Spring Data JPA
- Thymeleaf
- MySQL in production
- H2 for tests
- Flyway for database migration
- Maven for build and test
- QQ SMTP for email delivery
- `systemd` for production service management on Aliyun ECS

## 3. Main Runtime Profiles

### `dev`

Used for local development.

Configuration file:

```text
src/main/resources/application-dev.properties
```

### `test`

Used by automated tests.

Configuration file:

```text
src/main/resources/application-test.properties
```

### `prod`

Used on the Aliyun ECS server.

Configuration file:

```text
src/main/resources/application-prod.properties
```

Production uses database schema validation, so MySQL must match the Flyway migrations before startup.

## 4. Directory Responsibilities

### `controller`

Handles page routes and form submissions. Controllers are grouped by role:

- `controller/auth`
- `controller/customer`
- `controller/specialist`
- `controller/admin`

### `service`

Contains business rules, validation, and state transitions. Important rules include:

- email verification before login
- academic expert approval before specialist login
- booking slot conflict prevention
- booked slot locking
- booking status transitions
- fee calculation
- profile and avatar update handling

### `repository`

Contains database access through Spring Data JPA.

### `model`

Contains persisted entities:

- `User`
- `Specialist`
- `ExpertiseCategory`
- `AvailabilitySlot`
- `Booking`
- `BookingAuditLog`
- `Feedback`

### `enums`

Contains controlled status and role values:

- `RoleType`
- `GenderType`
- `SpecialistStatus`
- `CategoryStatus`
- `BookingStatus`

### `templates`

Contains Thymeleaf pages grouped by role:

- `auth`
- `customer`
- `specialist`
- `admin`
- `error`
- `fragments`

### `static`

Contains CSS, JavaScript, and static image assets.

### `db/migration`

Contains Flyway production migrations.

### `db/test-migration`

Contains test schema migration for H2.

## 5. Local Development Commands

Run tests:

```bash
./mvnw -q test
```

Build jar without tests:

```bash
./mvnw clean package -DskipTests
```

Run locally with default profile:

```bash
./mvnw spring-boot:run
```

Run locally with production-like profile:

```bash
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
```

## 6. Demo Accounts

The seeded administrator account is:

```text
Username: admin
Password: admin123
```

The login page also displays these demo administrator credentials for assessment convenience.

## 7. Mail Behaviour

The application sends:

- registration verification email
- password reset email
- academic expert rejection email

The application does not send a separate login email after academic expert approval.

## 8. Development Rules

- Keep business logic in services, not Thymeleaf templates.
- Keep security rules in `SecurityConfig` and user status checks in `UserService`.
- Use enums for role and status values.
- Add or update tests when changing business rules.
- Add Flyway migrations for database schema changes.
- Do not commit real production credentials.
- Do not commit generated files such as `target`, logs, uploads, or `.DS_Store`.

## 9. Final Verification Before Deployment

Before deploying a new jar:

```bash
./mvnw -q test
./mvnw clean package -DskipTests
```

Then copy the jar to `/opt/xsbooking/` and restart the `xsbooking` service on Aliyun.
