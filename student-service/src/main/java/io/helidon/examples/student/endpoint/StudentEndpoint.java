package io.helidon.examples.student.endpoint;

import io.helidon.examples.student.model.Course;
import io.helidon.examples.student.model.EnrollmentRequest;
import io.helidon.examples.student.model.Student;
import io.helidon.examples.student.repository.StudentRepository;
import io.helidon.http.Http;
import io.helidon.http.Status;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.RestServer;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Helidon Declarative REST Endpoint for Student Management.
 *
 * Helidon Declarative APIs (4.3+):
 * - @Service.Singleton: Registers this endpoint as a singleton service
 * - @RestServer.Endpoint: Marks this class as a REST endpoint for auto-discovery
 * - @Http.Path: Defines the base path for this endpoint
 * - Uses annotations instead of imperative routing code
 * - Provides automatic dependency injection via constructor
 * - Generates optimized code at compile-time (no reflection)
 * - Offers type-safe parameter binding
 * - Simplifies REST endpoint development
 *
 * API Endpoints:
 * - GET    /api/students              - List all students
 * - GET    /api/students/{id}         - Get student by ID
 * - GET    /api/students/search?name= - Search by name
 * - POST   /api/students              - Create new student
 * - PUT    /api/students/{id}         - Update student
 * - DELETE /api/students/{id}         - Delete student
 * - POST   /api/students/{id}/enroll  - Enroll in course
 * - GET    /api/students/stats        - Get statistics
 *
 * Benefits over imperative SE:
 * - NO manual routing setup (HttpRules)
 * - NO HttpService interface implementation
 * - Cleaner, more readable code
 * - Automatic parameter extraction and type conversion
 * - Better IDE support with annotations
 */
@Service.Singleton
@RestServer.Endpoint
@Http.Path("/api/students")
public class StudentEndpoint {

    private final StudentRepository repository;

    /**
     * Constructor with dependency injection.
     *
     * Helidon Declarative APIs (4.3+):
     * - StudentRepository is injected automatically
     * - No @Service.Inject needed - @Service.Singleton on class handles it
     * - No manual Services.get() lookup required
     */
    public StudentEndpoint(StudentRepository repository) {
        this.repository = repository;
    }

    /**
     * GET /api/students - List all students ordered by last name and first name.
     *
     * Declarative features:
     * - @Http.GET: HTTP GET method
     * - @Http.Path("/"): Relative path (combined with class-level path)
     * - ServerRequest/ServerResponse: Auto-provided parameters
     * - Automatic JSON serialization of List<Student>
     */
    @Http.GET
    @Http.Path("/")
    public void listStudents(ServerRequest req, ServerResponse res) {
        try {
            List<Student> students = repository.listAllOrderByLastNameAscFirstName();
            res.send(students);
        } catch (Exception e) {
            handleError(res, e, "Error listing students");
        }
    }

    /**
     * GET /api/students/{id} - Get student by ID.
     *
     * Declarative features:
     * - @Http.PathParam("id"): Automatic path parameter extraction and type conversion
     * - Type-safe parameter binding (String → Long)
     */
    @Http.GET
    @Http.Path("/{id}")
    public void getStudent(ServerRequest req,
                          ServerResponse res,
                          @Http.PathParam("id") String idParam) {
        try {
            Long id = Long.parseLong(idParam);
            Optional<Student> student = repository.findById(id);

            if (student.isPresent()) {
                res.send(student.get());
            } else {
                res.status(Status.NOT_FOUND_404)
                   .send(createErrorResponse("Student not found with id: " + id));
            }
        } catch (NumberFormatException e) {
            res.status(Status.BAD_REQUEST_400)
               .send(createErrorResponse("Invalid student ID format"));
        } catch (Exception e) {
            handleError(res, e, "Error retrieving student");
        }
    }

    /**
     * GET /api/students/search?name=xyz - Search students by name.
     *
     * Declarative features:
     * - @Http.QueryParam("name"): Automatic query parameter extraction
     * - Optional<String>: Helidon provides Optional for optional parameters
     *
     * Note: The path "/search" must come before "/{id}" in routing precedence,
     * which Helidon handles automatically based on specificity.
     */
    @Http.GET
    @Http.Path("/search")
    public void searchStudents(ServerRequest req,
                              ServerResponse res,
                              @Http.QueryParam("name") String nameParam) {
        try {
            if (nameParam == null || nameParam.trim().isEmpty()) {
                res.status(Status.BAD_REQUEST_400)
                   .send(createErrorResponse("Query parameter 'name' is required"));
                return;
            }

            List<Student> students = repository.findByNameContaining(nameParam);
            res.send(students);
        } catch (Exception e) {
            handleError(res, e, "Error searching students");
        }
    }

    /**
     * POST /api/students - Create new student.
     *
     * Declarative features:
     * - @Http.POST: HTTP POST method
     * - @Http.Entity: Automatic request body deserialization
     * - Type-safe entity binding (JSON → Student object)
     */
    @Http.POST
    @Http.Path("/")
    public void createStudent(ServerRequest req,
                             ServerResponse res,
                             @Http.Entity Student student) {
        try {
            // Validation
            if (student.getFirstName() == null || student.getFirstName().trim().isEmpty()) {
                res.status(Status.BAD_REQUEST_400)
                   .send(createErrorResponse("First name is required"));
                return;
            }
            if (student.getLastName() == null || student.getLastName().trim().isEmpty()) {
                res.status(Status.BAD_REQUEST_400)
                   .send(createErrorResponse("Last name is required"));
                return;
            }
            if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
                res.status(Status.BAD_REQUEST_400)
                   .send(createErrorResponse("Email is required"));
                return;
            }

            Student saved = repository.save(student);
            res.status(Status.CREATED_201).send(saved);
        } catch (Exception e) {
            handleError(res, e, "Error creating student");
        }
    }

    /**
     * PUT /api/students/{id} - Update student.
     *
     * Combines @Http.PathParam and @Http.Entity for path parameter and request body.
     */
    @Http.PUT
    @Http.Path("/{id}")
    public void updateStudent(ServerRequest req,
                             ServerResponse res,
                             @Http.PathParam("id") String idParam,
                             @Http.Entity Student updates) {
        try {
            Long id = Long.parseLong(idParam);

            Optional<Student> existingOpt = repository.findById(id);
            if (existingOpt.isEmpty()) {
                res.status(Status.NOT_FOUND_404)
                   .send(createErrorResponse("Student not found with id: " + id));
                return;
            }

            Student existing = existingOpt.get();

            // Update fields
            if (updates.getFirstName() != null) {
                existing.setFirstName(updates.getFirstName());
            }
            if (updates.getLastName() != null) {
                existing.setLastName(updates.getLastName());
            }
            if (updates.getEmail() != null) {
                existing.setEmail(updates.getEmail());
            }

            Student saved = repository.save(existing);
            res.send(saved);
        } catch (NumberFormatException e) {
            res.status(Status.BAD_REQUEST_400)
               .send(createErrorResponse("Invalid student ID format"));
        } catch (Exception e) {
            handleError(res, e, "Error updating student");
        }
    }

    /**
     * DELETE /api/students/{id} - Delete student.
     */
    @Http.DELETE
    @Http.Path("/{id}")
    public void deleteStudent(ServerRequest req,
                             ServerResponse res,
                             @Http.PathParam("id") String idParam) {
        try {
            Long id = Long.parseLong(idParam);

            // Check if student exists before attempting delete
            if (!repository.existsById(id)) {
                res.status(Status.NOT_FOUND_404)
                   .send(createErrorResponse("Student not found with id: " + id));
                return;
            }

            // Delete the student
            repository.deleteById(id);
            res.status(Status.NO_CONTENT_204).send();
        } catch (NumberFormatException e) {
            res.status(Status.BAD_REQUEST_400)
               .send(createErrorResponse("Invalid student ID format"));
        } catch (Exception e) {
            handleError(res, e, "Error deleting student");
        }
    }

    /**
     * POST /api/students/{id}/enroll - Enroll student in a course.
     */
    @Http.POST
    @Http.Path("/{id}/enroll")
    public void enrollStudent(ServerRequest req,
                             ServerResponse res,
                             @Http.PathParam("id") String idParam,
                             @Http.Entity EnrollmentRequest enrollmentReq) {
        try {
            Long id = Long.parseLong(idParam);

            if (enrollmentReq.getCourse() == null) {
                res.status(Status.BAD_REQUEST_400)
                   .send(createErrorResponse("Course is required"));
                return;
            }

            Optional<Student> studentOpt = repository.findById(id);
            if (studentOpt.isEmpty()) {
                res.status(Status.NOT_FOUND_404)
                   .send(createErrorResponse("Student not found with id: " + id));
                return;
            }

            Student student = studentOpt.get();
            student.enrollInCourse(enrollmentReq.getCourse());
            Student saved = repository.save(student);

            res.send(saved);
        } catch (NumberFormatException e) {
            res.status(Status.BAD_REQUEST_400)
               .send(createErrorResponse("Invalid student ID format"));
        } catch (Exception e) {
            handleError(res, e, "Error enrolling student");
        }
    }

    /**
     * GET /api/students/stats - Get enrollment statistics.
     *
     * Demonstrates multiple Helidon Data query methods working with Declarative endpoints.
     */
    @Http.GET
    @Http.Path("/stats")
    public void getStats(ServerRequest req, ServerResponse res) {
        try {
            long totalStudents = repository.count();
            long enrolledStudents = repository.countEnrolledStudents();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalStudents", totalStudents);
            stats.put("enrolledStudents", enrolledStudents);
            stats.put("unenrolledStudents", totalStudents - enrolledStudents);

            // Count by course using custom query method
            Map<String, Integer> byCourse = new HashMap<>();
            for (Course course : Course.values()) {
                int count = repository.findByCourse(course).size();
                if (count > 0) {
                    byCourse.put(course.name(), count);
                }
            }
            stats.put("enrollmentsByCourse", byCourse);

            res.send(stats);
        } catch (Exception e) {
            handleError(res, e, "Error generating statistics");
        }
    }

    /**
     * Helper method to create error response.
     */
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

    /**
     * Helper method to handle exceptions.
     */
    private void handleError(ServerResponse res, Exception e, String message) {
        e.printStackTrace();
        res.status(Status.INTERNAL_SERVER_ERROR_500)
           .send(createErrorResponse(message + ": " + e.getMessage()));
    }
}
