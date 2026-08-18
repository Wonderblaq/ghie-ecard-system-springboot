# GhIE Student E-Card Generation and Distribution System

A distributed microservices system built for the Ghana Institution of Engineering (GhIE). The platform handles student and Professional Engineer registration,administration portal(Role Based Access), automated membership digital ID card generation, email delivery, and verification.

---

## Architecture Overview

The system is split into two core backend services behind an Nginx reverse proxy, running fully containerized on DigitalOcean via Docker Compose.

```
               [ Client Requests ]
                        │
                        ▼
               [ Nginx Reverse Proxy ]
              (SSL / HTTPS / Port 80/443)
                        │
         ┌──────────────┴──────────────┐
         ▼                             ▼
[ Spring Boot App ]           [ Python FastAPI Service ]
(Port 8080)                   (Port 8000)
- Auth & Business Logic       - ID Card Image Engine
- Database Ops (PostgreSQL)   - Canvas & Dynamic Text Render with PIL
- Email Service (Brevo)       - QR Code Generator

```
## FULL DESCRIPTION

A full-stack digital student membership and E-Card management platform built for the Ghana Institution of Engineering (GhIE) Student Chapter.

The system manages student registration, verification, digital E-Card generation, email notifications, and administrative workflows through a microservice-based architecture.

## Architecture

The platform consists of three main services:

### 1. Spring Boot Backend
The core REST API responsible for:
- Student registration and profile management
- Professional Engineers registration
- Authentication and role-based authorization
- PostgreSQL database operations
- Registration approval/rejection workflows
- Transactional email notifications
- Communication with the Python card-generation service(MicroService )
- Logging and Auditing

### 2. Python FastAPI Service
A dedicated image-processing microservice responsible for:
- Dynamic E-Card generation
- Student information and photo processing
- QR-code generation and embedding
- Image manipulation using Pillow

Spring Boot communicates with this service internally through REST APIs.

### 3. Face ID Validation System *(Under Development)*
An AI-powered verification component being developed to improve student identity and photo validation.

The system is intended to:
- Detect and validate faces in submitted passport photographs
- Verify that uploaded images meet registration requirements
- Detect unsuitable images such as unclear or invalid submissions
- Provide an additional layer of identity verification for E-Card registration

## Technology Stack

- **Java 17-24 / Spring Boot**
- **Spring Data JPA / Hibernate**
- **Python 3.11 -14 / FastAPI**
- **PostgreSQL**
- **Pillow / OpenCV**
- **Docker & Docker Compose**
- **Nginx**
- **Certbot**
- **Brevo SMTP**
- **DigitalOcean**
- **REST APIs**
- **QR Code Generation**

## System Workflow

1. Student submits their registration details.
2. Spring Boot validates and stores the registration in PostgreSQL.
3. Submitted photos can be processed through the Face ID validation pipeline.
4. Once approved, Spring Boot sends student information to the FastAPI card-generation service.
5. FastAPI generates the personalized digital E-Card with the student's details, photo, and verification QR code.
6. The generated card is returned to Spring Boot.
7. The card and student records are updated.
8. The E-Card is delivered to the student's email through Brevo SMTP.

##  Deployment

The application is containerized using Docker Compose and deployed on a Linux-based DigitalOcean server.

Nginx serves as the reverse proxy and handles HTTPS/TLS termination using Certbot.

```bash
git pull origin main
docker compose down
docker compose up -d --build
