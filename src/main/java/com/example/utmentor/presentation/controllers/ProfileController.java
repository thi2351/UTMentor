package com.example.utmentor.presentation.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.utmentor.models.webModels.profile.GetIdResponse;
import com.example.utmentor.models.webModels.profile.ProfileInfoResponse;
import com.example.utmentor.models.webModels.profile.TutorReviewsResponse;
import com.example.utmentor.services.ProfileService;

@RestController
@RequestMapping("/api")
@CrossOrigin(
    origins = {"http://localhost:5173"},
    allowCredentials = "true"
)
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/info")
    public ResponseEntity<ProfileInfoResponse> getProfileInfo(
            @RequestParam String id) {
        ProfileInfoResponse response = profileService.getProfileInfo(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-id")
    public ResponseEntity<GetIdResponse> getUserIdByUsername(@RequestParam String username) {
        GetIdResponse response = profileService.getUserIdByUsername(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info/tutor-reviews")
    public ResponseEntity<TutorReviewsResponse> getTutorReviews(
            @RequestParam String id,
            @RequestParam int page,
            @RequestParam(required = false, defaultValue = "5") int pageSize,
            @RequestParam(required = false, defaultValue = "latest") String sort) {
        try {
            var reviews = profileService.getTutorReviews(id, page, pageSize, sort);
            TutorReviewsResponse response = new TutorReviewsResponse(reviews);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/info/tutor-rating-distribution")
    public ResponseEntity<Map<String, Map<Integer, Integer>>> getTutorRatingDistribution(
            @RequestParam String id) {
            var distribution = profileService.getTutorRatingDistribution(id);
            Map<String, Map<Integer, Integer>> response = new HashMap<>();
            response.put("rating", distribution);
            return ResponseEntity.ok(response);
    }

}