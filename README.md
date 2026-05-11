# XJTLU Academic Expert Appointment System

A Spring Boot and Thymeleaf web application for the 2026 CPT202 Group 1 coursework project.

The system supports XJTLU students and staff in booking consultations with campus academic experts. It also provides specialist-side schedule management and administrator-side review, category, expert, booking, and billing management.

## 1. Main Roles

- Customer: searches experts, submits booking requests, tracks status, reschedules or cancels bookings, and submits feedback.
- Academic Expert: manages availability, reviews assigned bookings, marks consultations as completed, and views feedback.
- Administrator: manages academic areas, expert records, expert approvals, booking review, audit logs, and billing.

## 2. Core Features

- Email-password registration and email verification
- Login, logout, forgot password, and reset password
- Customer and academic expert profile management
- Avatar upload and display
- Academic expert registration with administrator approval
- Academic area management
- Expert search and filtering by keyword, academic area, and availability
- Availability slot creation, editing, deletion, conflict prevention, and booked-slot locking
- Booking request creation and double-booking prevention
- Booking confirmation, rejection, cancellation, and rescheduling
- Booking audit logs and recent operation summaries
- Specialist consultation completion
- Customer feedback
- Automated booking fee calculation
- User-facing 400, 403, 404, and 500 error pages

## 3. Technology Stack

| Layer | Technology |
| --- | --- |
| Backend | Java, Spring Boot 4, Spring MVC |
| Security | Spring Security |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL for production, H2 for tests |
| Migration | Flyway |
| Frontend | Thymeleaf, CSS, vanilla JavaScript |
| Email | QQ SMTP |
| Build | Maven |
| Deployment | Aliyun ECS, Spring Boot jar, systemd |

## 4. Demo Account

Administrator demo account:

```text
Username: admin
Password: admin123
```

The login page also displays this demo administrator account for assessment convenience.

## 5. Local Development

Run tests:

```bash
./mvnw -q test
```

Build the jar:

```bash
./mvnw clean package -DskipTests
```

Run locally:

```bash
./mvnw spring-boot:run
```

The default local profile is `dev`.

## 6. Production Deployment Summary

Current deployment target:

```text
http://47.97.155.89:8080
```

Aliyun deployment uses:

- Spring Boot jar: `/opt/xsbooking/booking-0.0.1-SNAPSHOT.jar`
- systemd service: `xsbooking`
- MySQL database: `cpt202_booking`
- Database user: `booking_app`
- Avatar upload directory: `/opt/xsbooking/uploads/avatars`

Common deployment commands:

```bash
mvn clean package -DskipTests
sudo cp target/booking-0.0.1-SNAPSHOT.jar /opt/xsbooking/
sudo systemctl restart xsbooking
sudo systemctl status xsbooking --no-pager -l
```

More details are in [docs/ALIYUN_DEPLOYMENT.md](docs/ALIYUN_DEPLOYMENT.md).

## 7. Recommended Demonstration Flow

### Booking Flow

1. Customer logs in.
2. Customer searches academic experts.
3. Customer opens an expert detail page.
4. Customer selects an available slot.
5. Customer submits a booking request.
6. Administrator reviews the pending booking.
7. Administrator confirms or rejects the booking.
8. Customer checks the updated booking status.
9. Specialist views the assigned booking and schedule.
10. Specialist marks the consultation as completed.
11. Customer submits feedback.
12. Specialist views the feedback.

### Academic Expert Registration Flow

1. Open registration page.
2. Select academic expert role.
3. Fill in account and expert profile information.
4. Upload avatar.
5. Submit registration.
6. Complete email verification.
7. Try logging in before approval and show pending approval.
8. Administrator approves the pending expert.
9. Expert logs in successfully.
10. Expert creates availability slots.
11. Customer searches and books the new expert.

## 8. Documentation

- [Aliyun Deployment Guide](docs/ALIYUN_DEPLOYMENT.md)
- [Development Guide](docs/DEVELOPMENT_GUIDE.md)
- [Module Boundaries](docs/MODULE_BOUNDARIES.md)
- [Project Readiness Checklist](docs/PHASE1_CHECKLIST.md)
- [Team Development and Demonstration Plan](docs/TEAM_DEVELOPMENT_PLAN.md)


