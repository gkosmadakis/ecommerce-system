# 🛒 E-Commerce Microservices System

A distributed microservices-based e-commerce backend built with Spring Boot, Kafka, and Docker.

---

## 🧱 Architecture Overview

This system consists of multiple independent services communicating via Apache Kafka.

### Services

| Service               | Description |
|----------------------|------------|
| order-service        | Handles order creation, persistence, and event publishing |
| notification-service | Consumes order events and sends notifications |
| analytics-service    | Consumes events and builds analytics data |
| postgres             | Stores order data |
| mongo                | Stores analytics / notifications (optional) |
| kafka                | Event streaming platform |
| zookeeper            | Kafka coordination |

### **Build and Run Instructions**
In the root folder of the project where the packaging pom exists run:
 * mvn clean install
 * docker compose up -d
---
### **Tech Stack**

* Java 17
* Spring Boot 3.4.x
* Spring Data JPA
* Spring Kafka
* PostgreSQL
* MongoDB
* Apache Kafka
* Testcontainers
* Docker & Docker Compose

## 🔁 Event Flow

```text
Client → Order Service → PostgreSQL
                     ↓
                  Kafka Topic (order-created)
                     ↓
     ┌───────────────┴───────────────┐
     ↓                               ↓
Notification Service         Analytics Service
