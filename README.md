# Expert Consultancy Appointment System

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.5-green.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Scrum](https://img.shields.io/badge/Methodology-Scrum-orange.svg)](https://www.scrum.org/)

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
| 📱 **Responsive UI** | Bootstrap 5 optimized for desktop |

---

## 🛠️ Tech Stack
| Layer | Technologies |
|-------|--------------|
| **Backend** | Spring Boot 4.0.5 • Spring MVC • Spring Security • Spring Data JPA • Lombok |
| **Database** | MySQL 8.0 (Production) • H2 (Testing) |
| **Frontend** | Thymeleaf 3.1 • Bootstrap 5.3 • jQuery 3.7 • FullCalendar |
| **DevOps** | Maven • Git • Docker |
| **Methodology** | Scrum • GitHub Projects (Kanban) • User Story Mapping |

---

## 🚀 Quick Start

### Prerequisites
- JDK 17+
- MySQL 8.0+
- Maven 3.9+
- Git

### Installation
```bash
# Enter the project directory
cd Specialist-Consultation-Booking-System-main

# Configure database (edit src/main/resources/application.properties)
spring.datasource.url=jdbc:mysql://localhost:3306/consultancy_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD

# Build and run
mvn clean package
java -jar target/booking-0.0.1-SNAPSHOT.jar

# OR run directly (dev mode)
mvn spring-boot:run
```
