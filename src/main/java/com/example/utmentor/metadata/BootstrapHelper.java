package com.example.utmentor.metadata;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.utmentor.models.webModels.profile.Achievement;
import com.example.utmentor.models.webModels.profile.AchievementType;

/**
 * Utility class providing helper methods for bootstrap operations.
 */
@Component
public class BootstrapHelper {

    /**
     * Extract the local part (username) from an email address.
     */
    public String getLocalPart(String email) {
        if (email == null) return null;
        int at = email.indexOf('@');
        if (at <= 0) return null;
        return email.substring(0, at);
    }

    /**
     * Generate a phone number based on an ID hash.
     */
    public String generatePhoneNumber(String id) {
        int hash = id.hashCode();
        long phoneNum = 900000000L + (Math.abs(hash) % 100000000L);
        return "0" + phoneNum;
    }

    /**
     * Generate a review ID in the format "rev_" + alphanumeric string.
     */
    public String generateReviewId() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "rev_" + uuid.substring(0, 13).toUpperCase();
    }

    /**
     * Parse a timestamp string to Instant.
     */
    public Instant parseTimestamp(String timestampStr) {
        return OffsetDateTime.parse(timestampStr).toInstant();
    }

    /**
     * Create achievements for a tutor based on years of experience.
     */
    public List<Achievement> createTutorAchievements(int yearsOfExperience) {
        List<Achievement> achievements = new ArrayList<>();

        if (yearsOfExperience >= 5) {
            achievements.add(new Achievement(
                    UUID.randomUUID().toString(),
                    "Distinguished Teaching Award",
                    "Recognized for outstanding teaching excellence and student mentorship over 5+ years.",
                    "2023",
                    AchievementType.AWARD
            ));
        }

        if (yearsOfExperience >= 3) {
            achievements.add(new Achievement(
                    UUID.randomUUID().toString(),
                    "Advanced Software Engineering Certificate",
                    "Completed professional certification in modern software development practices.",
                    "2022",
                    AchievementType.CERTIFICATION
            ));
        }

        if (yearsOfExperience >= 2) {
            achievements.add(new Achievement(
                    UUID.randomUUID().toString(),
                    "Best Mentor Award",
                    "Awarded for exceptional mentorship and student success rate.",
                    "2024",
                    AchievementType.AWARD
            ));
        }

        if (achievements.isEmpty()) {
            achievements.add(new Achievement(
                    UUID.randomUUID().toString(),
                    "Teaching Assistant Certificate",
                    "Certified teaching assistant for undergraduate courses.",
                    "2025",
                    AchievementType.CERTIFICATION
            ));
        }

        return achievements;
    }

    /**
     * Create achievements for a student based on GPA.
     */
    public List<Achievement> createStudentAchievements(double gpa) {
        List<Achievement> achievements = new ArrayList<>();

        if (gpa >= 3.7) {
            achievements.add(new Achievement(
                    UUID.randomUUID().toString(),
                    "Dean's List",
                    "Recognized for academic excellence with GPA ≥ 3.7.",
                    "2025",
                    AchievementType.AWARD
            ));
        }

        if (gpa >= 3.5) {
            achievements.add(new Achievement(
                    UUID.randomUUID().toString(),
                    "Data Structures & Algorithms Specialization",
                    "Completed advanced DSA course with distinction.",
                    "2024",
                    AchievementType.CERTIFICATION
            ));
        }

        if (gpa >= 3.0) {
            achievements.add(new Achievement(
                    UUID.randomUUID().toString(),
                    "Programming Competition - Top 10",
                    "Achieved top 10 position in university programming contest.",
                    "2024",
                    AchievementType.AWARD
            ));
        }

        if (achievements.isEmpty()) {
            achievements.add(new Achievement(
                    UUID.randomUUID().toString(),
                    "Introduction to Computer Science",
                    "Successfully completed fundamental CS course.",
                    "2025",
                    AchievementType.CERTIFICATION
            ));
        }

        return achievements;
    }
}

