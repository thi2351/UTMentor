package com.example.utmentor.services;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.utmentor.models.docEntities.Expertise;
import com.example.utmentor.models.webModels.profile.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.example.utmentor.infrastructures.repository.Interface.RatingRepository;
import com.example.utmentor.infrastructures.repository.Interface.StudentProfileRepository;
import com.example.utmentor.infrastructures.repository.Interface.TutorProfileRepository;
import com.example.utmentor.infrastructures.repository.Interface.UserRepository;
import com.example.utmentor.models.docEntities.Department;
import com.example.utmentor.models.docEntities.Expertise;
import com.example.utmentor.models.docEntities.Rating;
import com.example.utmentor.models.docEntities.users.StudentProfile;
import com.example.utmentor.models.docEntities.users.TutorProfile;
import com.example.utmentor.models.docEntities.users.User;
import com.example.utmentor.models.webModels.PageResponse;
import com.example.utmentor.models.webModels.profile.GetIdResponse;
import com.example.utmentor.models.webModels.profile.ProfileInfoResponse;
import com.example.utmentor.models.webModels.profile.ReviewResponse;
import com.example.utmentor.models.webModels.search.TutorListItem;
import com.example.utmentor.util.Errors;
import com.example.utmentor.util.ValidatorException;

@Service
public class ProfileService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private GoogleCloudStorageService googleCloudStorageService;


    public ProfileInfoResponse getProfileInfo(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidatorException(Errors.USER_NOT_FOUND));

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
    

    public PageResponse<ReviewResponse> getTutorReviews(String tutorId, int page, int pageSize, String sort) {
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new ValidatorException(Errors.USER_NOT_FOUND));

        if (!tutor.hasTutorProfile()) {
            throw new ValidatorException(Errors.USER_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), Math.min(Math.max(1, pageSize), 100));
        return ratingRepository.findTutorReviewsWithReviewerInfo(tutorId, pageable, sort);
    }

    public Map<Integer,Integer> getTutorRatingDistribution(String tutorId) {
        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new ValidatorException(Errors.USER_NOT_FOUND));

        if (!tutor.hasTutorProfile()) {
            throw new ValidatorException(Errors.USER_NOT_FOUND);
        }

        List<Rating> ratings = ratingRepository.findByRevieweeID(tutorId);

        Map<Integer, Integer> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0);
        }

        for (Rating rating : ratings) {
            Integer ratingValue = rating.getRating();
            if (ratingValue != null && ratingValue >= 1 && ratingValue <= 5) {
                distribution.put(ratingValue, distribution.get(ratingValue) + 1);
            }
        }

        return distribution;
    }

    public PageResponse<TutorListItem> searchTutors(
            String department,
            List<String> expertise,
            String sort,
            int page,
            int pageSize
    ) {
        ValidatorException validatorException = new ValidatorException("ProfileService");
        
        if (page < 1) {
            validatorException.add("page", "INVALID_PAGE", "Page must be greater than 0");
        }
        
        if (sort != null &&
                !sort.equals("rating-descending") &&
                !sort.equals("rating-ascending") &&
                !sort.equals("firstName-ascending") &&
                !sort.equals("firstName-descending") &&
                !sort.equals("tutor-time")) {
            validatorException.add("sort",
                    "INVALID_SORT",
                    "Sort must be 'rating-descending', 'rating-ascending', 'firstName-ascending', 'firstName-descending', or 'tutor-time'");
        }
        
        if (sort == null) {
            sort = "rating-descending";
        }
        
        Department departmentEnum = null;
        if (department != null) {
            try {
                departmentEnum = mapStringToDepartment(department);
            } catch (ValidatorException e) {
                validatorException.add("department", "INVALID_DEPARTMENT", e.getMessage());
            }
        }
        
        List<Expertise> expertiseEnums = null;
        if (expertise != null && !expertise.isEmpty()) {
            expertiseEnums = new java.util.ArrayList<>();
            for (int i = 0; i < expertise.size(); i++) {
                String exp = expertise.get(i);
                if (exp != null && !exp.trim().isEmpty()) {
                    try {
                        expertiseEnums.add(mapStringToExpertise(exp));
                    } catch (ValidatorException e) {
                        validatorException.add("expertise[" + i + "]", "INVALID_EXPERTISE", e.getMessage());
                    }
                }
            }
        }
        
        if (validatorException.hasAny()) {
            throw validatorException;
        }
        
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), Math.min(Math.max(1, pageSize), 100));
        return tutorProfileRepository.search(
            departmentEnum,
            expertiseEnums,
            sort,
            pageable
        );
    }
    
    private Expertise mapStringToExpertise(String exp) throws ValidatorException {
        try {
            return Expertise.valueOf(exp.trim());
        } catch (IllegalArgumentException e) {
            throw new ValidatorException("Invalid expertise: " + exp + ".");
        }
    }
    
    private Department mapStringToDepartment(String dept) throws ValidatorException {
        try {
            return Department.valueOf(dept.trim());
        } catch (IllegalArgumentException e) {
            throw new ValidatorException("Invalid department: " + dept + ".");
        }
    }

    public void updateUserPhoneNumberAndAvatarUrl(String userId, String phoneNumber, String avatarUrl) {
        Query query = new Query(Criteria.where("_id").is(userId));

        Update update = new Update();
        boolean hasUpdate = false;

        if (phoneNumber != null) {
            update.set("phoneNumber", phoneNumber);
            hasUpdate = true;
        }
        if (avatarUrl != null) {
            update.set("avatarUrl", avatarUrl);
            hasUpdate = true;
        }

        if (hasUpdate) {
            update.set("updateAt", Instant.now());
            mongoTemplate.updateFirst(query, update, User.class);
        } else {
            System.out.println("No fields to update");
        }
    }

    public void updateTutorProfileTutorDescriptionAndExpertise(String tutorId, String tutorDescription, List<Expertise> expertise, List<Achievement> achievements) {
        Query query = new Query(Criteria.where("_id").is(tutorId));

        Update update = new Update();
        update.set("tutorDescription", tutorDescription);
        update.set("expertise", expertise);
        update.set("achievements", achievements);
        update.set("updateAt", Instant.now());

        mongoTemplate.updateFirst(query, update, TutorProfile.class);
    }
    public UpdateProfileTutorResponse updateTutorProfile(String tutorId, MultipartFile avatarFile, String phoneNumber, String description, List<Expertise> expertise, List<Achievement> achievements) throws IOException {

        String avatarUrl = null;
        if (avatarFile != null && !avatarFile.isEmpty()) {
            avatarUrl = googleCloudStorageService.uploadFile(avatarFile,tutorId + "/avatar");
        }
        updateUserPhoneNumberAndAvatarUrl(tutorId, phoneNumber, avatarUrl);
        updateTutorProfileTutorDescriptionAndExpertise(tutorId, description, expertise, achievements);
        return new UpdateProfileTutorResponse("Cập nhật hồ sơ gia sư thành công");
    }
}