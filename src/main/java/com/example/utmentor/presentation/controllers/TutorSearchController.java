package com.example.utmentor.presentation.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.utmentor.models.webModels.PageResponse;
import com.example.utmentor.models.webModels.search.TutorListItem;
import com.example.utmentor.services.TutorSearchService;
import com.example.utmentor.util.ValidatorException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TutorSearchController {
    
    @Autowired
    private TutorSearchService tutorSearchService;
    
    @GetMapping("/search-tutor")
    public ResponseEntity<?> searchTutors(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String expertise,
            @RequestParam(required = false, defaultValue = "rating-descending") String sort,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int pageSize
    ) {
        try {
            // Parse expertise parameter
            List<String> expertiseList = null;
            if (expertise != null && !expertise.trim().isEmpty()) {
                expertiseList = Arrays.asList(expertise.split(","));
                // Trim whitespace from each expertise
                expertiseList = expertiseList.stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            }
            
            PageResponse<TutorListItem> result = tutorSearchService.searchTutors(
                department,
                expertiseList,
                sort,
                page,
                pageSize
            );
            
            // Check if no results found
            if (result.data().isEmpty()) {
                return ResponseEntity.ok()
                    .body(new EmptySearchResponse(
                        "No tutor match your criteria. Please try with less filter."
                    ));
            }
            
            return ResponseEntity.ok(result);
            
        } catch (ValidatorException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An error occurred while searching tutors"));
        }
    }
    
    // Helper classes for responses
    public record EmptySearchResponse(String message) {}
    public record ErrorResponse(String message) {}
}
