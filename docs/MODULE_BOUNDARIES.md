# Module Boundaries

This document describes the current implemented module boundaries for the XJTLU Academic Expert Appointment System.

## 1. User Access and Personal Information Management

Responsible for account foundation across all roles.

Included functions:

- Email-password registration
- Email verification
- Login
- Logout
- Password reset
- Customer profile editing
- Specialist avatar update
- Role-based dashboard routing

Main files:

- `AuthController`
- `UserService`
- `SecurityConfig`
- `customer/profile.html`
- `specialist/profile.html`
- `auth/*.html`

## 2. Specialist Information Management

Responsible for academic expert records and administrator review.

Included functions:

- Add academic expert
- Edit academic expert
- Activate/deactivate academic expert
- Approve pending expert registration
- Reject pending expert registration
- Delete academic expert when safe
- Prevent duplicate name and academic area combinations
- Search and filter experts in admin pages
- Show expert avatars in admin and customer pages

Main files:

- `AdminSpecialistController`
- `SpecialistService`
- `SpecialistRepository`
- `admin/specialists.html`
- `admin/specialist-edit.html`

## 3. Specialist Availability Management

Responsible for expert appointment slots.

Included functions:

- Create available time slots
- Edit available slots
- Delete available slots
- Prevent slot conflicts
- Prevent past slots
- Lock booked slots from modification
- Weekly schedule view

Main files:

- `SpecialistAvailabilityController`
- `AvailabilityService`
- `AvailabilitySlotRepository`
- `specialist/availability.html`
- `specialist/booking-history.html`

## 4. Booking Request Creation and Validation

Responsible for customer-facing appointment creation.

Included functions:

- Search bookable experts
- Filter by academic area and availability
- View expert detail
- Select available slot
- Submit booking request
- Prevent double booking
- Prevent past slot booking
- Create initial `PENDING` booking record

Main files:

- `CustomerSpecialistController`
- `CustomerBookingController`
- `BookingService`
- `customer/specialists.html`
- `customer/specialist-detail.html`
- `customer/create-booking.html`

## 5. Booking Workflow and Status Management

Responsible for approval, cancellation, rescheduling, locking, and fee calculation.

Included functions:

- Admin confirm booking
- Admin reject booking with reason
- Customer cancel booking
- Customer reschedule booking with same specialist
- Status transition tracking
- Booking audit logs
- Completion rule validation
- Automated fee calculation

Main files:

- `AdminBookingController`
- `CustomerBookingController`
- `BookingService`
- `BookingAuditLog`
- `admin/bookings.html`
- `admin/booking-detail.html`
- `customer/bookings.html`
- `customer/booking-detail.html`
- `customer/reschedule-booking.html`

## 6. Specialist Consultation and Feedback Management

Responsible for specialist-side consultation handling.

Included functions:

- Specialist dashboard
- Booking list
- Booking detail
- Weekly schedule
- Mark booking as completed after consultation time
- View completed booking history
- View customer feedback
- Recent booking notifications

Main files:

- `SpecialistDashboardController`
- `SpecialistBookingController`
- `SpecialistFeedbackController`
- `BookingService`
- `FeedbackService`
- `specialist/dashboard.html`
- `specialist/bookings.html`
- `specialist/booking-detail.html`
- `specialist/feedback.html`

## 7. Customer Booking and Tracking

Responsible for customer appointment visibility.

Included functions:

- Customer dashboard
- Upcoming appointments
- Booking history
- Booking search
- Booking detail
- Booking status tracking
- Customer feedback submission
- Recent notifications

Main files:

- `CustomerDashboardController`
- `CustomerBookingController`
- `FeedbackService`
- `customer/dashboard.html`
- `customer/bookings.html`
- `customer/booking-detail.html`

## 8. Expertise Category Management

Responsible for academic area setup.

Included functions:

- Create academic area
- View academic area list
- Update academic area
- Activate/deactivate academic area
- Prevent duplicate names after trimming whitespace
- Hide inactive categories from customer-side selection

Main files:

- `AdminCategoryController`
- `ExpertiseCategoryService`
- `ExpertiseCategoryRepository`
- `admin/categories.html`

## 9. Billing

Responsible for administrator billing overview.

Included functions:

- Calculate booking fee from slot duration and expert rate
- List billable completed bookings
- Show booking fee data for administrator review

Main files:

- `AdminBillingController`
- `BookingService`
- `admin/billing.html`

## 10. Shared UI and Error Handling

Responsible for layout, navigation, and user-facing error pages.

Included functions:

- Shared layout
- Role-aware navigation
- 400 error page
- 403 error page
- 404 error page
- 500 error page

Main files:

- `GlobalNavigationAdvice`
- `ErrorPageController`
- `fragments/layout.html`
- `error/*.html`
