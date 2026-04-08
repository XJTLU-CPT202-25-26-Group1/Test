# Development Guide

## 1. Project Goal

This project is organized to support incremental development of a complete specialist consultation booking system for:

- Customer
- Specialist
- Administrator / Operations Manager

The structure is designed so the team can deliver features in phases without rewriting the whole system each time.

## 2. Incremental Development Principles

- Build shared foundations first.
- Keep each feature in its own module boundary.
- Let front-end pages evolve independently from business logic.
- Let database schema evolve through migrations instead of ad hoc table changes.
- Keep DTO, entity, validation, and controller responsibilities separate.
- Add tests alongside each feature module.

## 3. Recommended Delivery Phases

### Phase 1: Foundation

- Security dependency and login flow
- User, Role, Profile entities
- Shared layout pages
- Basic database schema initialization

### Phase 2: Specialist Discovery + Booking Creation

- Specialist list/detail
- Category filter
- Availability slot model
- Customer booking creation

### Phase 3: Booking Management

- Customer booking history
- Cancel and reschedule
- Specialist schedule view
- Conflict validation

### Phase 4: Admin Workflow

- Specialist management
- Category management
- Pending booking review
- Booking status transitions

### Phase 5: Completion Features

- Feedback
- Billing
- Reporting/dashboard polish
- Error handling and test hardening

## 4. Directory Responsibilities

### `src/main/java/com/cpt202/booking/controller`

- Handles web requests and page/API entry points.
- Split by role for easier team ownership.

### `src/main/java/com/cpt202/booking/service`

- Holds business rules.
- Should be the main place for booking rules, schedule conflict checks, and status transitions.

### `src/main/java/com/cpt202/booking/repository`

- Holds persistence access.
- Keep query logic here instead of controllers.

### `src/main/java/com/cpt202/booking/model`

- Holds core entities and domain objects.

### `src/main/java/com/cpt202/booking/dto`

- Holds request/response models and search/filter objects.

### `src/main/java/com/cpt202/booking/security`

- Authentication and authorization support classes.

### `src/main/java/com/cpt202/booking/common`

- Shared constants, common response wrappers, and reusable utility code.

### `src/main/java/com/cpt202/booking/mapper`

- Optional mapping layer between DTO and entity.
- Useful once DTOs and entities begin diverging.

### `src/main/java/com/cpt202/booking/validator`

- Custom validation classes for booking conflicts, duplicate categories, and role-specific rules.

### `src/main/resources/templates`

- Role-based page templates.

### `src/main/resources/static`

- Role-based JS/CSS assets.

### `src/main/resources/db/migration`

- Database migration scripts.
- Add one migration per schema change.

### `src/test/java/com/cpt202/booking`

- Test packages should mirror the main feature areas.

## 5. Team Collaboration Rules

- One owner updates shared entities after team agreement.
- Use DTOs for page forms and request payloads.
- Do not put business logic in Thymeleaf templates or controllers.
- Do not change booking status strings in multiple places; use enums.
- Do not hardcode role names across many files; centralize them.
- Each feature should include at least one service test.

## 6. Suggested Branch Strategy

- `main`: stable integration branch
- `develop`: active integration branch
- `feature/auth-*`
- `feature/booking-*`
- `feature/specialist-*`
- `feature/admin-*`
- `feature/frontend-*`

## 7. Shared Files That Need Agreement First

- `User`
- `Role`
- `Booking`
- `AvailabilitySlot`
- `ExpertiseCategory`
- `BookingStatus`

These should be agreed before parallel implementation starts.
