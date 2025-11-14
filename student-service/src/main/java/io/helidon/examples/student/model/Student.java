package io.helidon.examples.student.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Student entity representing a student in the system.
 * This is a JPA entity that will be persisted to the Oracle database.
 *
 * Educational Note:
 * - @Entity marks this class as a JPA entity
 * - @Table specifies the database table name
 * - @Id and @GeneratedValue define the primary key strategy
 * - @ElementCollection stores the set of courses in a separate table
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    /**
     * Stores the courses a student is enrolled in.
     * @ElementCollection creates a separate join table for this collection.
     * @Enumerated(EnumType.STRING) stores the enum as a string (not ordinal).
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_courses",
                     joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "course")
    @Enumerated(EnumType.STRING)
    private Set<Course> enrolledCourses = new HashSet<>();

    /**
     * Default constructor required by JPA.
     */
    public Student() {
        this.enrollmentDate = LocalDate.now();
    }

    /**
     * Constructor for creating a new student.
     */
    public Student(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.enrollmentDate = LocalDate.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Set<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(Set<Course> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    /**
     * Business method to enroll a student in a course.
     */
    public void enrollInCourse(Course course) {
        this.enrolledCourses.add(course);
    }

    /**
     * Business method to drop a course.
     */
    public void dropCourse(Course course) {
        this.enrolledCourses.remove(course);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id) &&
               Objects.equals(email, student.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", enrollmentDate=" + enrollmentDate +
                ", enrolledCourses=" + enrolledCourses +
                '}';
    }
}
