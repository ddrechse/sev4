# Project Plan: Student Management Microservice

## **What This Service Does:**
A Student Management microservice providing CRUD operations for student records with course enrollment tracking. Students can be created, updated, searched by name, deleted, and enrolled in courses.

---

## **The API:**
```
GET    /api/students              # List all students
GET    /api/students/{id}         # Get student by ID
GET    /api/students/search?name= # Search by name (partial match)
POST   /api/students              # Create new student
PUT    /api/students/{id}         # Update student
DELETE /api/students/{id}         # Delete student
POST   /api/students/{id}/enroll  # Enroll student in a course
GET    /api/students/stats        # Get enrollment statistics

GET    /openapi                   # OpenAPI specification
GET    /metrics                   # Prometheus metrics endpoint
GET    /health                    # Health check
```

---

## **How It Demonstrates the 3 Features:**

### **1. Helidon Declarative APIs**
- Clean `@RestServer.Endpoint` and `@Http.*` annotations replace imperative routing
- Automatic parameter binding (`@Http.PathParam`, `@Http.QueryParam`)
- Type-safe request/response handling with automatic JSON serialization
- OpenAPI documentation generated automatically from annotations
- Shows the dramatic code reduction vs imperative style

### **2. Helidon Data**
- `StudentRepository` interface - zero SQL/JDBC boilerplate code
- Auto-generated queries from method names (e.g., `findByLastNameContaining`)
- Custom queries with `@Query` annotation for complex searches
- Built-in operations: pagination, counting, save, delete, findById
- Repository pattern abstracts data access layer

### **3. OpenTelemetry + Prometheus**
- Config-based telemetry setup in `application.yaml` (you configure)
- Automatic HTTP request tracing spans for all endpoints
- Custom metrics creation (enrollment counter example)
- Prometheus-compatible `/metrics` endpoint for scraping
- Demonstrates observability without code pollution

---

## **Project Structure:**

```
student-service/
├── pom.xml                                    # Maven with Helidon 4.3 dependencies
├── README.md                                  # Comprehensive learning guide
├── src/main/
│   ├── java/io/helidon/examples/student/
│   │   ├── Main.java                          # Application entry point
│   │   ├── model/
│   │   │   ├── Student.java                   # Entity with @Entity, @Table
│   │   │   ├── Course.java                    # Enum (CS, MATH, PHYSICS, etc.)
│   │   │   └── EnrollmentRequest.java         # DTO for enrollment
│   │   ├── repository/
│   │   │   └── StudentRepository.java         # Helidon Data repository
│   │   └── endpoint/
│   │       └── StudentEndpoint.java           # Declarative REST with OpenAPI
│   └── resources/
│       ├── application.yaml                   # Config with comments for you
│       └── META-INF/
│           └── microprofile-config.properties # Additional config with comments
```

---

## **Key Files to Create:**

### 1. **pom.xml** - Helidon 4.3 dependencies for:
   - Helidon Declarative (SE + HTTP Declarative)
   - Helidon Data (with Oracle driver)
   - OpenTelemetry + Prometheus metrics
   - OpenAPI support
   - Jackson for JSON

### 2. **Student.java** - JPA entity with:
   - id, firstName, lastName, email, enrollmentDate
   - Set<Course> enrolledCourses
   - Helidon Data annotations

### 3. **StudentRepository.java** - Shows:
   - Method name queries: `findByLastNameContaining(String)`
   - Custom queries: `findByCourse(Course)`
   - Counting: `countByEnrolledCoursesIsNotEmpty()`
   - Custom logic: `enrollInCourse(Long, Course)`

### 4. **StudentEndpoint.java** - Shows:
   - `@RestServer.Endpoint` + `@Http.Path("/api/students")`
   - All CRUD operations with proper HTTP methods
   - OpenAPI annotations (`@OpenAPI.Summary`, etc.)
   - Custom metrics integration
   - Exception handling

### 5. **Main.java** - Bootstrap with:
   - WebServer setup
   - OpenTelemetry initialization
   - Health checks
   - Metrics endpoint

### 6. **Config files** - With detailed comments showing:
   - Oracle DB connection properties (for you to fill in)
   - Telemetry/Prometheus configuration (for you to fill in)
   - Server port, OpenAPI settings
   - Example values as comments

### 7. **README.md** - Teaching guide with:
   - Learning objectives for each feature
   - Code walkthrough with explanations
   - API usage examples (curl commands)
   - How to view metrics in Prometheus
   - Exercises for students to extend the service

---

## **What Students Learn:**

### **From Declarative APIs:**
- How annotations replace routing boilerplate
- Type-safe parameter handling
- Automatic serialization/deserialization
- OpenAPI spec generation

### **From Helidon Data:**
- Repository pattern benefits
- Query derivation magic
- When to use custom queries
- Transaction boundaries

### **From OpenTelemetry:**
- Configuration-driven observability
- Tracing spans and context propagation
- Custom metrics creation
- Prometheus integration

---

## **Implementation Notes:**

- No Docker Compose (you'll configure your own environment)
- Database and metrics configuration in `application.yaml` and `microprofile-config.properties` with comments
- All files include extensive educational comments
- Focus on demonstrating the three key Helidon 4.3 features
- Simple, understandable domain model for students
