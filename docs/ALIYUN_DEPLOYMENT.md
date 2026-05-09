# Aliyun Deployment Guide

This project is deployed on an Alibaba Cloud ECS server as a Spring Boot jar managed by `systemd`.

## 1. Deployed Components

- Spring Boot application jar
- Thymeleaf front-end templates packaged inside the jar
- Static assets packaged inside the jar, including CSS, JavaScript, and images
- MySQL database named `cpt202_booking`
- QQ SMTP mail service for verification, password reset, and rejection notification emails
- Avatar upload directory for customer and academic expert profile images
- `systemd` service named `xsbooking`

## 2. Current Production Entry

- Public URL: `http://47.97.155.89:8080`
- Application service: `xsbooking`
- Application jar path: `/opt/xsbooking/booking-0.0.1-SNAPSHOT.jar`
- Default server port: `8080`

The project currently uses HTTP and a public IP address. A custom domain and HTTPS are not part of the current deployment.

## 3. Required Server Environment

The production service should run with these environment values:

```bash
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
DB_URL=jdbc:mysql://127.0.0.1:3306/cpt202_booking?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
DB_USERNAME=booking_app
DB_PASSWORD=<production database password>
DB_POOL_MAX_SIZE=30
DB_POOL_MIN_IDLE=10

MAIL_ENABLED=true
MAIL_HOST=smtp.qq.com
MAIL_PORT=587
MAIL_USERNAME=<qq mail account>
MAIL_PASSWORD=<qq mail smtp authorization code>
MAIL_FROM=<qq mail account>
APP_BASE_URL=http://47.97.155.89:8080

APP_AVATAR_UPLOAD_DIR=/opt/xsbooking/uploads/avatars
```

Do not commit real passwords or mail authorization codes to Git.

## 4. Build Locally

From the project root:

```bash
mvn clean package -DskipTests
```

The generated jar is:

```bash
target/booking-0.0.1-SNAPSHOT.jar
```

## 5. Update the ECS Deployment

Copy the new jar to the production application directory:

```bash
sudo cp target/booking-0.0.1-SNAPSHOT.jar /opt/xsbooking/
```

Restart the service:

```bash
sudo systemctl restart xsbooking
```

Check service status:

```bash
sudo systemctl status xsbooking --no-pager -l
```

Check recent logs:

```bash
sudo journalctl -u xsbooking -n 120 --no-pager
```

## 6. Database Notes

- Production profile uses `spring.jpa.hibernate.ddl-auto=validate`.
- Schema changes must be applied through Flyway migrations in `src/main/resources/db/migration`.
- If the application fails with a missing column error, check whether the latest migration has been applied to MySQL.
- Current migrations include user account fields, gender, avatar path, and seeded academic area data.

## 7. Mail Notes

The application sends:

- Email verification link after registration
- Password reset email
- Academic expert rejection notification

The application does not send an additional login email after academic expert approval.

## 8. Avatar Upload Notes

Uploaded avatars are stored outside the jar in the configured upload directory. The jar can be replaced during deployment without deleting uploaded avatars.

Recommended production path:

```bash
/opt/xsbooking/uploads/avatars
```

## 9. Final Deployment Check

After restart, verify:

- Home page opens at `http://47.97.155.89:8080`
- Login page displays demo admin credentials
- Admin login works with `admin / admin123`
- Customer can search academic experts
- Academic expert avatars load correctly
- Verification email links use `http://47.97.155.89:8080`
