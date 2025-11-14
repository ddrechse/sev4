# Student Management Service

A production-ready microservice built with **Helidon SE 4.3.1** demonstrating modern Java cloud-native patterns.

## 🎯 What This Service Demonstrates

1. **Helidon SE 4.3.1** - Lightweight, reactive microservices framework
2. **Oracle Database** - Enterprise database with UCP connection pooling
3. **JPA/Hibernate 6.6.5** - Full ORM with entity management
4. **RESTful API** - Complete CRUD operations
5. **OpenTelemetry** - Distributed tracing support
6. **Prometheus Metrics** - Production-grade observability
7. **Health Checks** - Kubernetes-ready probes
8. **Eureka Discovery** - Spring Cloud compatible
9. **MicroProfile Metrics API** - Compatible with MP environment

## 📋 Prerequisites

- **Java 21+**
- **Maven 3.8+**
- **Oracle Database** (local container, ATP, or existing instance)

## 🚀 Quick Start

### 1. Build

```bash
mvn clean package
```

### 2. Configure Database

Set environment variables:

```bash
export DB_URL="jdbc:oracle:thin:@//localhost:1521/freepdb1"
export DB_USER="student"
export DB_PASSWORD="Welcome12345"
```

Or edit `src/main/resources/META-INF/microprofile-config.properties`.

### 3. Run

```bash
java -jar target/student-service.jar
```

Access: `http://localhost:8080`

## 📚 API Endpoints

### Student Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/students` | List all students |
| GET | `/api/students/{id}` | Get student by ID |
| GET | `/api/students/search?name=` | Search by name |
| POST | `/api/students` | Create student |
| PUT | `/api/students/{id}` | Update student |
| DELETE | `/api/students/{id}` | Delete student |
| POST | `/api/students/{id}/enroll` | Enroll in course |
| GET | `/api/students/stats` | Statistics |

### Observability

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/observe/health` | Health check |
| GET | `/observe/metrics` | Prometheus metrics |

## 💡 Example Usage

**Create Student:**
```bash
curl -X POST http://localhost:8080/api/students \
  -H 'Content-Type: application/json' \
  -d '{"firstName": "John", "lastName": "Doe", "email": "john@example.com"}'
```

**List Students:**
```bash
curl http://localhost:8080/api/students
```

**Search:**
```bash
curl 'http://localhost:8080/api/students/search?name=john'
```

**Enroll in Course:**
```bash
curl -X POST http://localhost:8080/api/students/1/enroll \
  -H 'Content-Type: application/json' \
  -d '{"course": "COMPUTER_SCIENCE"}'
```

## 📊 Available Courses

- COMPUTER_SCIENCE
- MATHEMATICS
- PHYSICS
- CHEMISTRY
- BIOLOGY
- ENGINEERING
- BUSINESS
- ECONOMICS
- PSYCHOLOGY
- LITERATURE

## ⚙️ Configuration

### Database (microprofile-config.properties)

```properties
# Environment variable-based (recommended for K8s)
jakarta.persistence.jdbc.url=${DB_URL:jdbc:oracle:thin:@//localhost:1521/freepdb1}
jakarta.persistence.jdbc.user=${DB_USER:student}
jakarta.persistence.jdbc.password=${DB_PASSWORD:Welcome12345}

# Hibernate
hibernate.hbm2ddl.auto=create  # create, update, validate, none
hibernate.show_sql=true
```

### OpenTelemetry

```properties
otel.sdk.disabled=false
otel.service.name=student-service
otel.exporter.otlp.endpoint=http://localhost:4317
```

### Eureka (application.yaml)

```yaml
server:
  features:
    eureka:
      enabled: true
      client:
        base-uri: http://localhost:8761/eureka/
```

## 🐳 Docker

```bash
# Build
docker build -t student-service:1.0.0 .

# Run
docker run -d -p 8080:8080 \
  -e DB_URL="jdbc:oracle:thin:@//host.docker.internal:1521/XEPDB1" \
  -e DB_USER="student" \
  -e DB_PASSWORD="password" \
  student-service:1.0.0
```

## ☸️ Kubernetes

### Using JKube

```bash
mvn k8s:build k8s:push k8s:resource k8s:apply
```

### Manual

See `README.md` for full Kubernetes deployment YAML examples.

## 📁 Project Structure

```
src/main/java/io/helidon/examples/student/
├── Main.java                      # Entry point
├── model/
│   ├── Student.java               # JPA Entity
│   ├── Course.java                # Enum
│   └── EnrollmentRequest.java     # DTO
├── repository/
│   └── StudentRepository.java     # Data access
└── service/
    └── StudentService.java        # REST endpoints
```

## 🔍 Monitoring

**Health:**
```bash
curl http://localhost:8080/observe/health
```

**Metrics (Prometheus format):**
```bash
curl http://localhost:8080/observe/metrics
```

## 🧪 Local Oracle Database

```bash
docker run -d --name oracle-free -p 1521:1521 \
  -e ORACLE_PASSWORD=Welcome12345 \
  container-registry.oracle.com/database/free:latest
```

## 🎓 Educational Value

This project demonstrates:

- **Modern Java**: Records, Optional, Streams, try-with-resources
- **Enterprise Patterns**: Repository, Service layers, DTOs
- **Cloud-Native**: Health checks, metrics, tracing, externalized config
- **Database Best Practices**: JPA, transactions, connection pooling

## 📖 Documentation

- [Helidon Documentation](https://helidon.io/docs/v4)
- [Original README](README.md) - Generated archetype documentation

---

**Built with Helidon SE 4.3.1** ✨
