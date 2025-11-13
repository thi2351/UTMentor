package com.example.utmentor.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.utmentor.infrastructures.repository.Interface.RatingRepository;
import com.example.utmentor.infrastructures.repository.Interface.StudentProfileRepository;
import com.example.utmentor.infrastructures.repository.Interface.TutorProfileRepository;
import com.example.utmentor.infrastructures.repository.Interface.UserRepository;
import com.example.utmentor.infrastructures.repository.search.RatingSearchRepo;
import com.example.utmentor.models.docEntities.Rating;
import com.example.utmentor.models.docEntities.users.StudentProfile;
import com.example.utmentor.models.docEntities.users.TutorProfile;
import com.example.utmentor.models.docEntities.users.User;
import com.example.utmentor.models.webModels.PageResponse;
import com.example.utmentor.models.webModels.profile.GetIdResponse;
import com.example.utmentor.models.webModels.profile.ProfileInfoResponse;
import com.example.utmentor.models.webModels.profile.ReviewResponse;
import com.example.utmentor.util.Errors;
import com.example.utmentor.util.ValidatorException;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private RatingSearchRepo ratingSearchRepo;

    @Autowired
    private RatingRepository ratingRepository;

    public ProfileInfoResponse getProfileInfo(String userId) {
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

    public GetIdResponse getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ValidatorException(Errors.USER_NOT_FOUND));
        return new GetIdResponse(user.getId());
    }

    public PageResponse<ReviewResponse> getTutorReviews(String tutorId, int page, int pageSize, String sort) {
        // Validate tutor exists
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new ValidatorException(Errors.USER_NOT_FOUND));

        if (!tutor.hasTutorProfile()) {
            throw new ValidatorException(Errors.USER_NOT_FOUND);
        }

        // Use repository to get reviews with aggregation
        return ratingSearchRepo.findTutorReviewsWithReviewerInfo(tutorId, page, pageSize, sort);
    }

    public Map<Integer,Integer> getTutorRatingDistribution(String tutorId) {
        // Validate tutor exists
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new ValidatorException(Errors.USER_NOT_FOUND));

        if (!tutor.hasTutorProfile()) {
            throw new ValidatorException(Errors.USER_NOT_FOUND);
        }

        // Fetch all ratings for this tutor
        List<Rating> ratings = ratingRepository.findByRevieweeID(tutorId, Sort.unsorted());

        Map<Integer, Integer> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0);
        }

        // Count ratings by value
        for (Rating rating : ratings) {
            Integer ratingValue = rating.getRating();
            if (ratingValue != null && ratingValue >= 1 && ratingValue <= 5) {
                distribution.put(ratingValue, distribution.get(ratingValue) + 1);
            }
        }

        return distribution;
    }
}