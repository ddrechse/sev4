package io.helidon.examples.student.model;

/**
 * Enum representing available courses in the system.
 *
 * Educational Note:
 * Using an enum provides type safety and makes it easy to add/remove courses
 * without changing database schema (when using @Enumerated(EnumType.STRING)).
 */
public enum Course {
    COMPUTER_SCIENCE("Computer Science"),
    MATHEMATICS("Mathematics"),
    PHYSICS("Physics"),
    CHEMISTRY("Chemistry"),
    BIOLOGY("Biology"),
    ENGINEERING("Engineering"),
    BUSINESS("Business Administration"),
    ECONOMICS("Economics"),
    PSYCHOLOGY("Psychology"),
    LITERATURE("Literature");

    private final String displayName;

    Course(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
