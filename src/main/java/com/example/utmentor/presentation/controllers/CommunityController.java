package com.example.utmentor.presentation.controllers;


import com.example.utmentor.models.webModels.communities.GetCommunitiesResponse;
import com.example.utmentor.services.CommunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communities")
public class CommunityController {
    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping("/getCommunitiesInfoOfTutor")
    public ResponseEntity<?> getCommunityInfoOfTutor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentId = authentication.getName();
        var listOfCommunities =  communityService.getCommunityInfoOfTutor(currentId);
        Map<String, List<GetCommunitiesResponse>> response = Map.of("communities", listOfCommunities);
        return ResponseEntity.ok(response);
    }
}
