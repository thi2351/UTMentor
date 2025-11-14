package com.example.utmentor.presentation.controllers;

import com.example.utmentor.models.webModels.profile.GetIdResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.utmentor.services.UserService;
import com.example.utmentor.models.webModels.users.*;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get-id")
    public ResponseEntity<GetIdResponse> getUserIdByUsername(@RequestParam String username) {
        GetIdResponse response = userService.getUserIdByUsername(username);
        return ResponseEntity.ok(response);
    }

}
