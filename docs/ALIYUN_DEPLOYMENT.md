# Aliyun Deployment Guide

## Recommended Target

- ECS for application deployment
- ApsaraDB RDS for MySQL

## Runtime Strategy

- Build the Spring Boot jar locally or in CI
- Package it into Docker
- Run the container on Alibaba Cloud ECS

## Required Environment Variables

- `SPRING_PROFILES_ACTIVE=prod`
- `SERVER_PORT=8080`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `DB_POOL_MAX_SIZE`
- `DB_POOL_MIN_IDLE`

## Deployment Steps

### 1. Prepare Aliyun Resources

- Create one ECS instance
- Create one MySQL RDS instance
- Open ECS security group port `8080`
- If exposing publicly through Nginx, also open `80` and `443`

### 2. Prepare the Database

- Create database `cpt202_booking`
- Create application user with least privileges
- Put the connection info into environment variables

### 3. Build the Project

```bash
./mvnw clean package
```

### 4. Build Docker Image

```bash
docker build -t specialist-booking-system:latest .
```

### 5. Run on ECS

```bash
docker run -d \
  --name specialist-booking-system \
  -p 8080:8080 \
  --env-file .env \
  specialist-booking-system:latest
```

### 6. Optional Reverse Proxy

- Put Nginx in front of the container
- Proxy `80/443` to `127.0.0.1:8080`

## Notes

- Do not commit real production credentials
- Use RDS whitelist or VPC rules to allow only the ECS server
- In production, prefer HTTPS and a domain name
