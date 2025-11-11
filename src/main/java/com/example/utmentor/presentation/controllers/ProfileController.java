package com.example.utmentor.presentation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.utmentor.models.webModels.profile.GetIdResponse;
import com.example.utmentor.models.webModels.profile.ProfileInfoResponse;
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
}