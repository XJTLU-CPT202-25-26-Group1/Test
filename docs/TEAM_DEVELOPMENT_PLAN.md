# Team Development and Demonstration Plan

## 1. Current Team Ownership Areas

The project is split into these maintainable feature areas:

1. Authentication and user account management
2. Customer dashboard, search, and booking pages
3. Academic expert profile and availability management
4. Administrator expert and academic area management
5. Booking workflow and audit logging
6. Feedback and billing
7. UI layout, CSS, and front-end consistency
8. Testing, deployment, and documentation

## 2. Shared Review Rule

Changes to these files or areas should be reviewed carefully:

- `User`
- `Specialist`
- `Booking`
- `AvailabilitySlot`
- `ExpertiseCategory`
- `BookingStatus`
- `SpecialistStatus`
- `SecurityConfig`
- Flyway migration files
- Production configuration files

## 3. Demo Flow 1: Booking Flow

1. Customer logs in.
2. Customer opens the dashboard.
3. Customer opens the academic expert list.
4. Customer filters experts by academic area, keyword, or available date.
5. Customer opens an expert detail page.
6. Customer selects an available slot.
7. Customer fills in consultation topic and notes.
8. Customer submits the booking request.
9. Customer checks that the booking status is `PENDING`.
10. Administrator logs in with `admin / admin123`.
11. Administrator opens the bookings page.
12. Administrator confirms or rejects the pending booking.
13. Customer checks the updated booking status.
14. Specialist logs in.
15. Specialist checks booking list and schedule.
16. Specialist marks the consultation as completed after the appointment time.
17. Customer submits feedback.
18. Specialist views the feedback.

## 4. Demo Flow 2: Academic Expert Registration Flow

1. Open the registration page.
2. Select academic expert role.
3. Fill in account information.
4. Upload an avatar.
5. Select an academic area.
6. Fill in professional level, fee rate, and profile description.
7. Submit registration.
8. Open the verification email and complete email verification.
9. Try logging in before approval and show the pending approval message.
10. Administrator logs in.
11. Administrator opens the specialists page.
12. Administrator reviews the pending expert registration.
13. Administrator approves the expert.
14. Expert logs in successfully.
15. Expert creates availability slots.
16. Customer searches for the new expert and sees that the expert is now bookable.

## 5. Presentation Tips

- Start with the customer booking flow because it is the core user journey.
- Then show academic expert registration because it explains the approval workflow.
- Use the admin demo account shown on the login page.
- Keep each screen explanation short and focus on visible system behaviour.
- Mention that email verification is required before login.
- Mention that expert approval is required before specialist login.

## 6. Final Demo Checklist

Before the presentation:

- Restart the Aliyun service.
- Confirm the home page opens.
- Confirm admin login works.
- Confirm at least one active academic expert has an available future slot.
- Confirm mail sending is enabled if demonstrating registration verification.
- Confirm uploaded avatars display correctly.
- Keep one customer account and one specialist account ready as backup.
