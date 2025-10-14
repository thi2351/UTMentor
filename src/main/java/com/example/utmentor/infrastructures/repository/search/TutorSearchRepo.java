package com.example.utmentor.infrastructures.repository.search;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.example.utmentor.infrastructures.repository.Interface.UserRepository;
import com.example.utmentor.models.docEntities.Department;
import com.example.utmentor.models.docEntities.Expertise;
import com.example.utmentor.models.docEntities.users.TutorProfile;
import com.example.utmentor.models.docEntities.users.User;
import com.example.utmentor.models.webModels.PageResponse;
import com.example.utmentor.models.webModels.search.TutorListItem;


@Repository
public class TutorSearchRepo {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    public PageResponse<TutorListItem> search(
            Department department,
            List<Expertise> expertiseList,
            String sortKey,
            Integer page,
            Integer pageSize
    ) {
        int p = Math.max(1, page);
        int sz = Math.min(Math.max(1, pageSize), 100);
        
        // Build query for tutor profiles
        Query query = new Query();
        
        // Filter active tutors with available capacity
        query.addCriteria(Criteria.where("isActive").is(true));
        query.addCriteria(Criteria.where("currentMenteeCount").lt("maximumCapacity"));
        
        // Filter by expertise if provided
        if (expertiseList != null && !expertiseList.isEmpty()) {
            query.addCriteria(Criteria.where("expertise").all(expertiseList));
        }
        
        // Apply sorting
        Sort sort = getSort(sortKey);
        query.with(sort);
        
        // Get total count
        long total = mongoTemplate.count(query, TutorProfile.class);
        
        // Apply pagination
        Pageable pageable = PageRequest.of(p - 1, sz, sort);
        query.with(pageable);
        
        // Execute query
        List<TutorProfile> tutorProfiles = mongoTemplate.find(query, TutorProfile.class);
        
        // Convert to TutorListItem
        List<TutorListItem> items = new ArrayList<>();
        for (TutorProfile profile : tutorProfiles) {
            User user = userRepository.findById(profile.getId()).orElse(null);
            if (user != null && (department == null || user.getDepartment() == department)) {
                items.add(convertToTutorListItem(profile, user));
            }
        }
        
        // Calculate pagination info
        int totalPages = (int) Math.ceil((double) total / sz);
        boolean hasNext = p < totalPages;
        
        return new PageResponse<>(items, p, sz, total, totalPages, hasNext);
    }
    
    private Sort getSort(String sortKey) {
        if (sortKey == null) {
            sortKey = "rating-descending";
        }
        
        return switch (sortKey.toLowerCase()) {
            case "tutor-time" -> Sort.by(Sort.Order.desc("ratingCount"), Sort.Order.desc("ratingAvg"));
            default -> Sort.by(Sort.Order.desc("ratingAvg"), Sort.Order.desc("ratingCount"));
        };
    }
    
    private TutorListItem convertToTutorListItem(TutorProfile profile, User user) {
        return new TutorListItem(
            profile.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getAvatarUrl(),
            user.getDepartment(),
            profile.getExpertise(),
            profile.getRatingCount(),
            profile.getRatingAvg(),
            profile.getCurrentMenteeCount(),
            profile.getMaximumCapacity(),
            null // description not available in current model
        );
    }
}
