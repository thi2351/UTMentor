package com.example.utmentor.models.webModels.profile;

import com.example.utmentor.models.docEntities.Connection.StatusRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProfileInfoResponse(
    String firstName,
    String lastName,
    String avatarUrl,
    String email,
    String phoneNumber,
    String department,
    StudentProfileDTO studentProfile,
    TutorProfileDTO tutorProfile
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record StudentProfileDTO(
        String studentID,
        Double currentGPA,
        String studentDescription,
        List<String> learningGoal,
        List<Achievement> achievements,
        List<String> demandCourse
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TutorProfileDTO(
        Integer currMenteeCount,
        Integer maximumCapacity,
        String tutorDescription,
        List<String> expertise,
        Double ratingAvg,
        Integer ratingCount,
        Integer totalStudentTaught,
        Integer yearsOfExperience,
        List<Achievement> achievements,
        StatusRequest statusConnection
    ) { }
}