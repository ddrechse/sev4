# Student Service - Helidon SE Microservice

A modern, cloud-native microservice for managing student records built with Helidon SE 4.3.2. Features full CRUD operations, search capabilities, pagination, and enrollment tracking with Oracle Database backend.

## Features

- **Student Management**: Complete CRUD operations for student records
- **Search & Filter**: Search students by major, email, and other criteria
- **Pagination**: Efficient page-based data retrieval
- **Enrollment Tracking**: Track student enrollment dates and status
- **Statistics**: Get enrollment statistics and metrics
- **Health Checks**: Built-in Kubernetes-ready health endpoints
- **API Documentation**: OpenAPI 3.0 specification
- **Observability**: OpenTelemetry tracing support

## Technology Stack

### Core Framework
- **Helidon SE 4.3.2** - Lightweight, reactive microservices framework
- **Java 21** - Latest LTS with modern language features
- **Maven 3.9+** - Build and dependency management

### Data Layer
- **Helidon Data SQL** - Modern data access with compile-time query generation
- **Oracle Database 23.4 FREE** - Enterprise-grade database
- **Jakarta Persistence (JPA) 3.1.0** - Standard ORM API
- **EclipseLink 4.0.7** - JPA implementation
- **HikariCP 5.0.1** - High-performance connection pooling
- **Oracle JDBC 23.4.0.24.05** - ojdbc11 driver with UCP

### HTTP & Serialization
- **Helidon WebServer** - Native reactive HTTP server
- **Jackson 2.20.0** - JSON processing
- **JSON-P (Parsson)** - Jakarta JSON Processing

### Build & Deploy
- **Eclipse JKube 1.16.2** - Kubernetes-native Docker builds
- **Docker** - Container runtime

## Prerequisites

- **Java 21** or later ([Download](https://adoptium.net/))
- **Maven 3.9+** ([Download](https://maven.apache.org/download.cgi))
- **Docker** (for containerization) ([Download](https://www.docker.com/))
- **Oracle Database 23.4 FREE** ([Download](https://www.oracle.com/database/free/))

## Database Setup

### 1. Start Oracle Database

Using Docker:
```bash
docker run -d \
  --name oracle-free \
  -p 1521:1521 \
  -e ORACLE_PWD=Oracle123 \
  gvenzl/oracle-free:23.4-slim
```

### 2. Create Database User

```sql
-- Connect as SYSTEM
sqlplus system/Oracle123@//localhost:1521/FREEPDB1

-- Create user
CREATE USER student_user IDENTIFIED BY "student_pass";
GRANT CONNECT, RESOURCE TO student_user;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO student_user;
GRANT UNLIMITED TABLESPACE TO student_user;
```

### 3. Configure Connection

Edit `src/main/resources/application.yaml`:

```yaml
javax.sql.DataSource:
  student-db:
    dataSourceClassName: oracle.jdbc.pool.OracleDataSource
    dataSource:
      url: jdbc:oracle:thin:@//localhost:1521/FREEPDB1
      user: student_user
      password: student_pass  # Or use ${DB_PASSWORD} env var
```

## Building the Application

### Clean Build

```bash
mvn clean package
```

This creates:
- `target/student.jar` - Application JAR
- `target/student-deployment.zip` - Deployment package with all dependencies

### Skip Tests

```bash
mvn clean package -DskipTests
```

### Run Tests Only

```bash
mvn test
```

## Running the Application

### Local Execution

```bash
java -jar target/student.jar
```

The application starts on **http://localhost:7001**

### With Environment Variables

```bash
export DB_PASSWORD=student_pass
java -jar target/student.jar
```

### Docker Build

```bash
# Set Docker host (if using Rancher Desktop or Colima)
export DOCKER_HOST=unix:///Users/$USER/.rd/docker.sock

# Build image
mvn k8s:build
```

### Run Docker Container

```bash
docker run -d \
  --name student-service \
  -p 7001:7001 \
  -e DB_PASSWORD=student_pass \
  us-ashburn-1.ocir.io/maacloud/student:2.0-SNAPSHOT
```

## API Endpoints

### Student Operations

#### List All Students (Paginated)
```bash
curl http://localhost:7001/students
curl "http://localhost:7001/students?page=0&size=10"
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@university.edu",
      "major": "Computer Science",
      "enrollmentDate": "2024-09-01",
      "status": "ACTIVE"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "number": 0,
  "size": 10
}
```

#### Get Student by ID
```bash
curl http://localhost:7001/students/1
```

#### Search Students by Major
```bash
curl "http://localhost:7001/students/search?major=Computer%20Science"
```

#### Create Student
```bash
curl -X POST http://localhost:7001/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@university.edu",
    "major": "Mathematics",
    "enrollmentDate": "2024-09-01",
    "status": "ACTIVE"
  }'
```

#### Update Student
```bash
curl -X PUT http://localhost:7001/students/1 \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@university.edu",
    "major": "Data Science",
    "enrollmentDate": "2024-09-01",
    "status": "ACTIVE"
  }'
```

#### Delete Student
```bash
curl -X DELETE http://localhost:7001/students/1
```

#### Enroll Student in Course
```bash
curl -X POST http://localhost:7001/students/1/enroll \
  -H "Content-Type: application/json" \
  -d '{
    "courseId": "CS101",
    "courseName": "Introduction to Programming"
  }'
```

#### Get Enrollment Statistics
```bash
curl http://localhost:7001/students/stats
```

**Response:**
```json
{
  "totalStudents": 150,
  "activeStudents": 145,
  "enrollmentsByMajor": {
    "Computer Science": 45,
    "Mathematics": 30,
    "Engineering": 40,
    "Business": 35
  }
}
```

### Greeting Endpoints

```bash
# Get default greeting
curl http://localhost:7001/greet

# Get personalized greeting
curl http://localhost:7001/greet/World

# Update greeting
curl -X PUT http://localhost:7001/greet/greeting \
  -H "Content-Type: application/json" \
  -d '{"greeting": "Hola"}'
```

### Health & Observability

```bash
# Health check
curl http://localhost:7001/observe/health

# OpenAPI specification
curl http://localhost:7001/openapi
```

## Configuration

### Application Settings (`application.yaml`)

```yaml
# Server configuration
server:
  port: 7001
  host: 0.0.0.0

# Application settings
app:
  greeting: "Hello"

# Database configuration
javax.sql.DataSource:
  student-db:
    dataSourceClassName: oracle.jdbc.pool.OracleDataSource
    dataSource:
      url: jdbc:oracle:thin:@//localhost:1521/FREEPDB1
      user: student_user
      password: ${DB_PASSWORD}

    # HikariCP connection pool settings
    hikari:
      poolName: student-db
      connectionTimeout: 30000
      idleTimeout: 600000
      maxLifetime: 1800000
      maximumPoolSize: 10
      minimumIdle: 5
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_PASSWORD` | Database password | (required) |
| `SERVER_PORT` | HTTP server port | 7001 |
| `DB_URL` | JDBC connection URL | jdbc:oracle:thin:@//localhost:1521/FREEPDB1 |
| `DB_USER` | Database username | student_user |

## Architecture

### Service Injection Pattern

The application uses **Helidon's compile-time annotation processors** with **runtime service registration and discovery**:

- Annotation processors generate service descriptors at **compile-time**
- Service registration uses **EmptyBinding** pattern with ServiceLoader-based discovery
- Matches the standard pattern from official Helidon 4.x examples

```java
@Service.Singleton
@Http.Path("/students")
public class StudentEndpoint {
    private final StudentRepository repository;

    @Service.Inject
    StudentEndpoint(StudentRepository repository) {
        this.repository = repository;
    }
}
```

### Helidon Data Repository Pattern

Repositories use **compile-time query generation**:

```java
@DbRepository
public interface StudentRepository {
    Student findById(Long id);
    List<Student> findByMajor(String major);
    Page<Student> findAll(PageRequest pageRequest);
    Student save(Student student);
    void deleteById(Long id);
    long count();
}
```

Query methods are:
- ✅ Validated at compile-time
- ✅ Generated as bytecode (no runtime parsing)
- ✅ Type-safe with IDE support
- ✅ Convention-based (method names generate SQL)

### Annotation Processors

Two annotation processors run at compile-time:

1. **helidon-bundles-apt** - Processes `@Service.*`, `@Http.*`, `@DbRepository` annotations
2. **helidon-data-jakarta-persistence-codegen** - Generates repository implementations

## Project Structure

```
student-service/
├── src/
│   ├── main/
│   │   ├── java/io/helidon/examples/student/
│   │   │   ├── Main.java                    # Application entry point
│   │   │   ├── endpoint/
│   │   │   │   ├── StudentEndpoint.java     # Student REST API
│   │   │   │   └── GreetEndpoint.java       # Greeting API
│   │   │   ├── model/
│   │   │   │   ├── Student.java             # JPA entity
│   │   │   │   └── EnrollmentRequest.java   # DTO
│   │   │   └── repository/
│   │   │       └── StudentRepository.java   # Data access interface
│   │   └── resources/
│   │       ├── application.yaml             # Configuration
│   │       └── META-INF/
│   │           └── persistence.xml          # JPA configuration
│   ├── test/
│   │   └── java/io/helidon/examples/student/
│   │       └── MainTest.java                # Integration tests
│   └── assembly/
│       └── jib-ready.xml                    # Deployment package assembly
├── pom.xml                                  # Maven configuration
└── README.md                                # This file
```

## Connection Pool Tuning

### HikariCP Settings Explained

| Setting | Value | Purpose |
|---------|-------|---------|
| `maximumPoolSize` | 10 | Max concurrent database connections |
| `minimumIdle` | 5 | Connections kept alive during idle |
| `connectionTimeout` | 30s | Max wait time for connection |
| `idleTimeout` | 10min | Close idle connections after |
| `maxLifetime` | 30min | Max connection lifetime |

### Tuning Recommendations

**Low Traffic (< 100 req/min):**
```yaml
maximumPoolSize: 5
minimumIdle: 2
```

**Medium Traffic (100-1000 req/min):**
```yaml
maximumPoolSize: 10
minimumIdle: 5
```

**High Traffic (> 1000 req/min):**
```yaml
maximumPoolSize: 20
minimumIdle: 10
```

## Deployment

### Docker Image

- **Registry**: `us-ashburn-1.ocir.io/maacloud/student`
- **Tag**: `2.0-SNAPSHOT`
- **Size**: 517MB
- **Platform**: linux/arm64, linux/amd64

### Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: student-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: student-service
  template:
    metadata:
      labels:
        app: student-service
    spec:
      containers:
      - name: student-service
        image: us-ashburn-1.ocir.io/maacloud/student:2.0-SNAPSHOT
        ports:
        - containerPort: 7001
        env:
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: student-db-secret
              key: password
        livenessProbe:
          httpGet:
            path: /observe/health
            port: 7001
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /observe/health
            port: 7001
          initialDelaySeconds: 10
          periodSeconds: 5
```

## Troubleshooting

### Database Connection Issues

**Problem**: `Cannot connect to database`

**Solution**:
1. Verify Oracle database is running: `docker ps | grep oracle`
2. Check connection string in `application.yaml`
3. Test connection: `sqlplus student_user/student_pass@//localhost:1521/FREEPDB1`
4. Verify user grants: `SELECT * FROM USER_SYS_PRIVS;`

### Build Failures

**Problem**: Annotation processor errors

**Solution**:
```bash
# Clean Maven cache and rebuild
mvn clean install -U
```

### Docker Build Issues

**Problem**: `Cannot connect to Docker daemon`

**Solution**:
```bash
# Check Docker is running
docker ps

# Set DOCKER_HOST for Rancher Desktop/Colima
export DOCKER_HOST=unix://$HOME/.rd/docker.sock
```

## Performance Considerations

### Query Optimization

Helidon Data generates optimized SQL at compile-time:

```java
// Generates: SELECT * FROM students WHERE major = ?
List<Student> findByMajor(String major);

// Generates paginated query with OFFSET/FETCH
Page<Student> findAll(PageRequest pageRequest);
```

### Connection Pooling

HikariCP provides:
- Sub-millisecond connection acquisition
- Automatic connection leak detection
- JMX monitoring support

### Startup Time

- **Cold start**: ~3-5 seconds
- **With DB connection**: ~4-6 seconds
- Uses compile-time service discovery for fast startup

## Development

### Adding New Endpoints

1. Create endpoint class:
```java
@Service.Singleton
@Http.Path("/courses")
public class CourseEndpoint {
    @Http.GET
    public void list(ServerRequest req, ServerResponse res) {
        // Implementation
    }
}
```

2. Rebuild: `mvn clean package`

### Adding Repository Methods

```java
@DbRepository
public interface StudentRepository {
    // Method name convention generates query
    List<Student> findByLastNameOrderByFirstName(String lastName);

    Optional<Student> findByEmail(String email);

    long countByMajor(String major);
}
```

No implementation needed - Helidon Data generates it at compile-time!

## License

Copyright (c) 2024 Oracle and/or its affiliates.

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit changes: `git commit -am 'Add feature'`
4. Push to branch: `git push origin feature/my-feature`
5. Submit a pull request

## Support

- **Helidon Documentation**: https://helidon.io/docs/latest/
- **Helidon Examples**: https://github.com/helidon-io/helidon-examples
- **Issue Tracker**: [GitHub Issues](https://github.com/helidon-io/helidon/issues)

## Version History

- **2.0-SNAPSHOT** (Current)
  - Aligned with official Helidon 4.x patterns
  - Optimized dependency scopes
  - Enhanced connection pooling
  - Improved documentation

- **1.0-SNAPSHOT**
  - Initial release
  - Basic CRUD operations
  - Oracle database integration
