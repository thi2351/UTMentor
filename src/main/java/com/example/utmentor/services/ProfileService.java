package com.example.utmentor.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.utmentor.infrastructures.repository.Interface.StudentProfileRepository;
import com.example.utmentor.infrastructures.repository.Interface.TutorProfileRepository;
import com.example.utmentor.infrastructures.repository.Interface.UserRepository;
import com.example.utmentor.infrastructures.securities.JwtService;
import com.example.utmentor.models.docEntities.users.StudentProfile;
import com.example.utmentor.models.docEntities.users.TutorProfile;
import com.example.utmentor.models.docEntities.users.User;
import com.example.utmentor.models.webModels.profile.ProfileInfoResponse;
import com.example.utmentor.util.Errors;
import com.example.utmentor.util.ValidatorException;

import java.util.List;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private JwtService jwtService;

    public ProfileInfoResponse getProfileInfo(String userId, String authorization) {
        // Validate token if provided
        if (authorization != null && !authorization.isBlank()) {
            if (!authorization.startsWith("Bearer ")) {
                throw new ValidatorException(Errors.INVALID_TOKEN);
            }
            String token = authorization.substring(7);
            if (!jwtService.isTokenValid(token)) {
                throw new ValidatorException(Errors.INVALID_TOKEN);
            }
        }

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidatorException(Errors.USER_NOT_FOUND));

        // Get profiles
        ProfileInfoResponse.StudentProfileDTO studentDTO = null;
        if (user.hasStudentProfile()) {
            studentDTO = studentProfileRepository.findById(userId)
                    .map(this::mapToStudentDTO)
                    .orElse(null);
        }

        ProfileInfoResponse.TutorProfileDTO tutorDTO = null;
        if (user.hasTutorProfile()) {
            tutorDTO = tutorProfileRepository.findById(userId)
                    .map(this::mapToTutorDTO)
                    .orElse(null);
        }

        // Build response using record constructor
        return new ProfileInfoResponse(
            user.getFirstName(),
            user.getLastName(),
            user.getAvatarUrl(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getDepartment() != null ? user.getDepartment().name() : null,
            studentDTO,
            tutorDTO
        );
    }

    private ProfileInfoResponse.StudentProfileDTO mapToStudentDTO(StudentProfile profile) {
        return new ProfileInfoResponse.StudentProfileDTO(
            profile.getStudentID(),
            profile.getCurrentGPA(),
            profile.getStudentDescription(),
            profile.getLearningGoal(),
            profile.getAchievements() != null ? profile.getAchievements() : List.of(),
            profile.getDemandCourse() != null
                ? profile.getDemandCourse().stream().map(Enum::name).toList()
                : null
        );
    }

    private ProfileInfoResponse.TutorProfileDTO mapToTutorDTO(TutorProfile profile) {
        return new ProfileInfoResponse.TutorProfileDTO(
            profile.getCurrentMenteeCount(),
            profile.getMaximumCapacity(),
            profile.getTutorDescription(),
            profile.getExpertise() != null
                ? profile.getExpertise().stream().map(Enum::name).toList()
                : null,
            profile.getRatingAvg(),
            profile.getRatingCount(),
            profile.getTotalStudentTaught(),
            profile.getYearsOfExperience(),
            profile.getAchievements() != null ? profile.getAchievements() : List.of()
        );
    }
}