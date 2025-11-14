package io.helidon.examples.student.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for course enrollment requests.
 *
 * Educational Note:
 * DTOs (Data Transfer Objects) are used for API requests/responses
 * to decouple the API contract from the internal domain model.
 *
 * Using Jackson annotations for JSON serialization/deserialization.
 */
public class EnrollmentRequest {

    private final Course course;

    @JsonCreator
    public EnrollmentRequest(@JsonProperty("course") Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    @Override
    public String toString() {
        return "EnrollmentRequest{" +
                "course=" + course +
                '}';
    }
}
