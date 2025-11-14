package com.example.utmentor.presentation.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.utmentor.models.docEntities.Expertise;
import com.example.utmentor.models.webModels.profile.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.utmentor.services.ProfileService;
import org.springframework.web.multipart.MultipartFile;

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
    @PutMapping(value ="/profile/tutor/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpdateProfileTutorResponse> updateTutorProfile(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("description") String description,
            @RequestParam(value = "expertise", required = false) String expertiseJson,
            @RequestParam(value = "achievements", required = false) String achievementsJson,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile
    ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Achievement> achievements = new ArrayList<>();
        List<Expertise> expertises = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        var id = authentication.getName();

        if(expertiseJson != null) {
            expertises = objectMapper.readValue(expertiseJson, new TypeReference<List<Expertise>>() {});
        }


        if (achievementsJson != null) {
            achievements = objectMapper.readValue(achievementsJson, new TypeReference<List<Achievement>>() {});
        }
        var response= profileService.updateTutorProfile(id,  avatarFile, phoneNumber, description, expertises, achievements);
        return ResponseEntity.ok(response);
    }

}