package com.example.utmentor.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.utmentor.infrastructures.repository.search.TutorSearchRepo;
import com.example.utmentor.models.docEntities.Department;
import com.example.utmentor.models.docEntities.Expertise;
import com.example.utmentor.models.webModels.PageResponse;
import com.example.utmentor.models.webModels.search.TutorListItem;
import com.example.utmentor.util.ValidatorException;

@Service
public class TutorSearchService {
    
    @Autowired
    private TutorSearchRepo tutorSearchRepo;
    
    public PageResponse<TutorListItem> searchTutors(
            String department,
            List<String> expertise,
            String sort,
            int page,
            int pageSize
    ) {
        // Create ValidatorException for collecting all validation errors
        ValidatorException validatorException = new ValidatorException("TutorSearchService");
        
        // Validate page parameters
        if (page < 1) {
            validatorException.add("page", "INVALID_PAGE", "Page must be greater than 0");
        }
        
//        if (pageSize != 10 && pageSize != 20 && pageSize != 50) {
//            validatorException.add("pageSize",
//            "INVALID_PAGE_SIZE",
//            "Page size must be 10, 20, or 50");
//        }
//
        // Validate sort parameter
        if (sort != null && !sort.equals("rating-descending") && !sort.equals("tutor-time")) {
            validatorException.add("sort", 
            "INVALID_SORT", 
            "Sort must be 'rating-descending' or 'tutor-time'");
        }
        
        // Set default sort
        if (sort == null) {
            sort = "rating-descending";
        }
        
        // Validate and convert department
        Department departmentEnum = null;
        if (department != null) {
            try {
                departmentEnum = mapStringToDepartment(department);
            } catch (ValidatorException e) {
                validatorException.add("department", "INVALID_DEPARTMENT", e.getMessage());
            }
        }
        
        // Validate and convert expertise strings to enums
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
        
        // Throw exception if any validation errors occurred
        if (validatorException.hasAny()) {
            throw validatorException;
        }
        
        // Use TutorSearchRepo for optimized search
        return tutorSearchRepo.search(
            departmentEnum,
            expertiseEnums,
            sort,
            page,
            pageSize
        );
    }
    
    /**
     * Maps string input to Expertise enum
     * Note: exp is guaranteed to be non-null and non-empty by caller
     */
    private Expertise mapStringToExpertise(String exp) throws ValidatorException {
        try {
            return Expertise.valueOf(exp.trim());
        } catch (IllegalArgumentException e) {
            throw new ValidatorException("Invalid expertise: " + exp + ".");
        }
    }
    
    /**
     * Maps string input to Department enum
     * Note: dept is guaranteed to be non-null and non-empty by caller
     */
    private Department mapStringToDepartment(String dept) throws ValidatorException {
        try {
            return Department.valueOf(dept.trim());
        } catch (IllegalArgumentException e) {
            throw new ValidatorException("Invalid department: " + dept + ".");
        }
    }
}
