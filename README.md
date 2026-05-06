# Expert Consultancy Appointment System

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.5-green.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

> A consultation booking platform built with Spring Boot and Thymeleaf for the XJTLU CPT202 Software Engineering group project. The system supports customer, specialist, and administrator workflows.

---

## ✨ Core Features
| Module | Description |
|--------|-------------|
| 🔐 **Multi-Role Auth** | Role-based access control (Admin/Specialist/Customer) with Spring Security |
| 📅 **Booking Workflow** | Calendar-based slot selection, conflict detection, fee calculation |
| 👥 **Specialist Mgmt** | Expert profiles, expertise categories, availability management |
| 💰 **Billing System** | Dynamic pricing rules by expertise level & category |
| 📧 **Email Support** | Verification, password reset, and appointment status email flow |
| 📱 **Responsive UI** | Role-based pages with custom responsive styling |

---

## 🛠️ Tech Stack
| Layer | Technologies |
|-------|--------------|
| **Backend** | Spring Boot 4.0.5 • Spring MVC • Spring Security • Spring Data JPA • Lombok |
| **Database** | MySQL 8.0 (Production) • H2 (Testing) |
| **Frontend** | Thymeleaf templates • custom CSS • vanilla JavaScript |
| **DevOps** | Maven Wrapper • Git • Docker |

---

## 🚀 Quick Start

### Prerequisites
- JDK 17+
- MySQL 8.0+
- Git

### Installation
```bash
# Enter the project directory
cd Specialist-Consultation-Booking-System-main

# Configure database (edit src/main/resources/application.properties)
spring.datasource.url=jdbc:mysql://localhost:3306/cpt202_booking?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD

# Build and run
./mvnw clean package
java -jar target/booking-0.0.1-SNAPSHOT.jar

# OR run directly (dev mode)
./mvnw spring-boot:run
```
