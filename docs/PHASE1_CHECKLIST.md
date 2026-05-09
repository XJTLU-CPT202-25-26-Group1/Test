# Project Readiness Checklist

This file replaces the original phase-one checklist. The project is now in final coursework/demo readiness state.

## 1. Completed Foundation

- Spring Boot project structure
- Role-based controller structure
- Thymeleaf layout and pages
- Spring Security login/logout
- Database-backed user accounts
- Flyway migrations
- MySQL production profile
- H2 test profile
- Maven test and package workflow
- Aliyun ECS jar deployment
- `systemd` service deployment

## 2. Completed User Features

- Customer registration
- Academic expert registration
- Email verification
- Password reset
- Login failure messages
- Profile editing
- Avatar upload and display
- Gender limited to Male/Female

## 3. Completed Customer Features

- Customer dashboard
- Expert search and filtering
- Expert detail page
- Slot selection
- Booking request creation
- Booking history
- Booking status tracking
- Booking detail
- Rescheduling
- Cancellation
- Feedback submission

## 4. Completed Specialist Features

- Specialist dashboard
- Availability slot creation
- Availability slot update and deletion
- Conflict prevention
- Booked slot lock
- Weekly schedule
- Booking list and detail
- Mark completed
- Completed history
- Feedback view
- Avatar update

## 5. Completed Admin Features

- Admin dashboard
- Academic area management
- Academic expert management
- Pending expert approval/rejection
- Safe expert deletion
- Booking approval/rejection
- Recent booking overview
- Recent audit log overview
- Billing page

## 6. Completed Reliability Checks

- Past slot prevention
- Same-day past slot prevention
- Double booking prevention
- Reschedule limited to the same academic expert
- Completion blocked before consultation time
- Duplicate academic area prevention
- Duplicate expert name and area prevention
- Booked slots cannot be modified
- User-facing 400/403/404/500 error pages

## 7. Final Verification Commands

Run before submitting or deploying:

```bash
./mvnw -q test
./mvnw clean package -DskipTests
```

## 8. Known Deployment Facts

- Production access: `http://47.97.155.89:8080`
- Service name: `xsbooking`
- Jar path: `/opt/xsbooking/booking-0.0.1-SNAPSHOT.jar`
- Database: `cpt202_booking`
- Application database user: `booking_app`
- Demo admin: `admin / admin123`

## 9. Demonstration Scope

The recommended demonstration should focus on:

- Customer booking flow
- Academic expert registration and approval flow

These two flows cover all three roles and most core PBIs.
