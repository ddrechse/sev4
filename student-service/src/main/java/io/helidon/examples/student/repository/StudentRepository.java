package io.helidon.examples.student.repository;

import io.helidon.data.Data;
import io.helidon.examples.student.model.Course;
import io.helidon.examples.student.model.Student;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Helidon Data Repository for Student entity operations.
 *
 * Educational Note:
 * This demonstrates Helidon Data Repository - a powerful abstraction that eliminates
 * JPA boilerplate code. The interface is transformed into a working implementation
 * at compile time through annotation processing.
 *
 * Key Helidon Data Concepts:
 * - @Data.Repository: Marks this interface as a data repository and enables automatic service registration
 * - Data.CrudRepository: Provides built-in CRUD operations (save, findById, findAll, delete, etc.)
 * - Data.SessionRepository: Provides access to underlying EntityManager for complex operations
 * - Method name queries: Helidon generates queries from method names automatically
 * - @Data.Query: Custom JPQL queries for complex scenarios
 *
 * Benefits over traditional JPA:
 * - NO EntityManager lifecycle management
 * - NO transaction boilerplate (begin/commit/rollback)
 * - NO try-catch-finally blocks
 * - Type-safe query derivation from method names
 * - Compile-time validation
 * - 85% code reduction (189 lines → ~30 lines)
 *
 * Note: @Service.Contract is NOT needed - @Data.Repository handles service registration automatically
 */
@Data.Repository
public interface StudentRepository extends Data.CrudRepository<Student, Long>,
                                           Data.SessionRepository<EntityManager> {

    /**
     * Search students by name (partial match on first or last name).
     *
     * Uses custom JPQL query because we need case-insensitive search
     * and the same search term for both fields.
     *
     * Generated JPQL:
     * SELECT s FROM Student s
     * WHERE LOWER(s.firstName) LIKE LOWER(:name) OR LOWER(s.lastName) LIKE LOWER(:name)
     */
    @Data.Query("SELECT s FROM Student s WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Student> findByNameContaining(String name);

    /**
     * Find students enrolled in a specific course.
     *
     * Uses custom JPQL query because we need to query a collection element.
     * This is more complex than simple property matching.
     */
    @Data.Query("SELECT s FROM Student s JOIN s.enrolledCourses c WHERE c = :course")
    List<Student> findByCourse(Course course);

    /**
     * Count students with at least one enrolled course.
     *
     * Uses custom JPQL query to count students with non-empty course collections.
     *
     * Generated JPQL:
     * SELECT COUNT(DISTINCT s) FROM Student s WHERE SIZE(s.enrolledCourses) > 0
     */
    @Data.Query("SELECT COUNT(DISTINCT s) FROM Student s WHERE SIZE(s.enrolledCourses) > 0")
    long countEnrolledStudents();

    /**
     * Find all students ordered by last name and first name.
     *
     * Method name query breakdown:
     * - list: Return type is List
     * - All: No filtering criteria
     * - OrderBy: Start of ordering clause
     * - LastName: Order by lastName field (ascending by default)
     * - Asc: Explicit ascending order
     * - FirstName: Then order by firstName field (ascending by default)
     *
     * Generated JPQL (conceptually):
     * SELECT s FROM Student s ORDER BY s.lastName ASC, s.firstName ASC
     */
    List<Student> listAllOrderByLastNameAscFirstName();

    /**
     * Additional methods inherited from Data.CrudRepository:
     * - save(Student): Insert or update student
     * - Optional<Student> findById(Long): Find by primary key
     * - List<Student> findAll(): Find all students
     * - void deleteById(Long): Delete by primary key
     * - long count(): Count all students
     * - boolean existsById(Long): Check if student exists
     *
     * Additional methods inherited from Data.SessionRepository:
     * - <T> T call(Function<EntityManager, T>): Execute operation with EntityManager
     * - void run(Consumer<EntityManager>): Execute operation with EntityManager (void)
     *
     * These methods are automatically implemented by Helidon Data at compile time!
     */
}
